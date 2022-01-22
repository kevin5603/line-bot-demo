package com.kevin.linebotdemo.controller;


import com.kevin.linebotdemo.model.BusQueryRule;
import com.kevin.linebotdemo.model.LineMissionRequest;
import com.kevin.linebotdemo.model.LineMissionResponse;
import com.kevin.linebotdemo.model.dto.BusDto;
import com.kevin.linebotdemo.model.dto.StationBusDto;
import com.kevin.linebotdemo.service.BusAPIService;
import com.kevin.linebotdemo.service.BusKeywordService;
import com.kevin.linebotdemo.service.KeywordService;
import com.kevin.linebotdemo.service.line.RegisterLineService;
import com.linecorp.bot.client.LineMessagingClient;
import com.linecorp.bot.model.ReplyMessage;
import com.linecorp.bot.model.event.Event;
import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.model.message.Message;
import com.linecorp.bot.model.response.BotApiResponse;
import com.linecorp.bot.spring.boot.annotation.EventMapping;
import com.linecorp.bot.spring.boot.annotation.LineMessageHandler;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static com.kevin.linebotdemo.config.BusAPIConst.SPACE;


/**
 * 監聽Line事件，當事件發生依需求傳遞給不同service處理後續事項
 *
 * @author liyanting
 */
@LineMessageHandler
@AllArgsConstructor
@Slf4j
public class LineEventController {

    private final LineMessagingClient lineMessagingClient;
    private final RegisterLineService registerLineService;
    private final KeywordService keywordService;
    private final BusKeywordService busKeywordService;
    private final BusAPIService busAPIService;

    @EventMapping
    public void handleTextMessageEvent(MessageEvent<TextMessageContent> event) throws Exception {
        TextMessageContent message = event.getMessage();
        handleTextContent(event.getReplyToken(), event, message);
    }

    private void handleTextContent(String replyToken, Event event, TextMessageContent content)
            throws Exception {
        String userId = event.getSource().getUserId();
        val lineMissionRequest = new LineMissionRequest(content, userId);

        val lineMissionResponse = missionDispatcher(lineMissionRequest);

    }


    private LineMissionResponse missionDispatcher(LineMissionRequest<TextMessageContent> lineMissionRequest) {
        val lineMessage = lineMissionRequest.getContent().getText();
        val userId = lineMissionRequest.getUserId();
        String headString = getCommand(lineMessage);

        LineMissionResponse lineMissionResponse;
        String message = filterHeader(lineMessage);
        switch (headString) {
            case "註冊":
                lineMissionResponse = registerCommand(userId, message);
                break;
            case "[註冊]":
            default:
                lineMissionResponse = executeCommand(userId, lineMessage);
        }
        return lineMissionResponse;
    }

    /**
     * 擷取最前面字段來判斷要做哪個動作
     * @param lineMessage line訊息
     * @return APP指令
     */
    private String getCommand(String lineMessage) {
        return lineMessage.split(SPACE)[0];
    }

    /**
     * 使用者在Line輸入關鍵字 回傳公車到站資訊給使用者
     * @param userId 使用者ID
     * @param keyword 關鍵字
     * @return LineMissionResponse
     */
    public LineMissionResponse executeCommand(String userId, String keyword) {
        // 依場站再分群組
        Map<String, List<StationBusDto>> stationBusDtoGroupByStationCode = groupByStationCode(userId, keyword);
        List<BusDto> l = new ArrayList<>();

        // 再將各群組組成一個bus api query
        for (String station: stationBusDtoGroupByStationCode.keySet()) {
            List<StationBusDto> list = stationBusDtoGroupByStationCode.get(station);
            BusQueryRule busQueryRule = combineQuery(list);
            // 各自call BusAPIService
            WebClient.ResponseSpec data = busAPIService.getData(busAPIService.getBusEstimateTime(busQueryRule));
            data.bodyToFlux(BusDto.class).subscribe(l::add);
        }

        // 打包結果回傳
        val busDtoLineMissionResponse = new LineMissionResponse<List<BusDto>>();
        busDtoLineMissionResponse.setBody(l);
        return busDtoLineMissionResponse;
    }

    /**
     * 依場站代碼在做區分等等要依照場站代碼分別區Call公共運輸API
     * @param userId
     * @param keyword
     * @return
     */
    protected Map<String, List<StationBusDto>> groupByStationCode(String userId, String keyword) {
        List<StationBusDto> stationBusDtoList = busKeywordService.findByUserIdAndKeyword(userId, keyword);
        Map<String, List<StationBusDto>> collect = stationBusDtoList
                .stream()
                .collect(Collectors.groupingBy(StationBusDto::getStationCode));
        return collect;
    }

    public BusQueryRule combineQuery(List<StationBusDto> list) {
        BusQueryRule rule = new BusQueryRule();
        rule.setSelect("StopName,RouteName");
        String collect = list.stream().map(dto -> String.format("RouteName/Zh_tw eq '%s'", dto.getBusName()))
                .collect(Collectors.joining(" or "));
        rule.setFilter(collect);
        return rule;
    }

    /**
     * 註冊關鍵字
     * @param userId Line使用者ID
     * @param message 用戶訊息
     * @return
     */
    private LineMissionResponse registerCommand(String userId, String message) {
        return registerLineService.registerCommand(userId, message);
    }

    /**
     * 過濾開頭指令
     * @param lineMessage
     * @return
     */
    private String filterHeader(String lineMessage) {
        return lineMessage.substring(lineMessage.indexOf(SPACE) + 1);
    }

    private void reply(@NonNull String replyToken, @NonNull List<Message> messages) {
        reply(replyToken, messages, false);
    }

    private void reply(@NonNull String replyToken,
                       @NonNull List<Message> messages,
                       boolean notificationDisabled) {
        try {
            BotApiResponse apiResponse = lineMessagingClient
                    .replyMessage(new ReplyMessage(replyToken, messages, notificationDisabled))
                    .get();
            log.info("Sent messages: {}", apiResponse);
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
}
