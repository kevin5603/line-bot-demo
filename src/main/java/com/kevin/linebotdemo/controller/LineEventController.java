package com.kevin.linebotdemo.controller;


import com.kevin.linebotdemo.config.LineResponseCode;
import com.kevin.linebotdemo.model.BusQueryRule;
import com.kevin.linebotdemo.model.LineMissionRequest;
import com.kevin.linebotdemo.model.LineMissionResponse;
import com.kevin.linebotdemo.model.StationGroup;
import com.kevin.linebotdemo.model.dto.BusDto;
import com.kevin.linebotdemo.model.dto.StationBusDto;
import com.kevin.linebotdemo.service.*;
import com.kevin.linebotdemo.service.line.RegisterLineService;
import com.linecorp.bot.client.LineMessagingClient;
import com.linecorp.bot.model.PushMessage;
import com.linecorp.bot.model.ReplyMessage;
import com.linecorp.bot.model.event.Event;
import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.model.message.Message;
import com.linecorp.bot.model.message.TextMessage;
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
import static com.kevin.linebotdemo.config.LineResponseCode.SUCCESS;


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
    private final UserService userService;
    private final StationGroupService stationGroupService;
    private final RedisService redisService;

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
        val lineUserId = lineMissionRequest.getUserId();
        String headString = getCommand(lineMessage);

        LineMissionResponse lineMissionResponse;
        String message = filterHeader(lineMessage);
        switch (headString) {
            case "註冊":
                lineMissionResponse = registerCommand(lineUserId, message);
                break;
            case "[場站]":
                lineMissionResponse = registerCommand(lineUserId, message, true);
                pushMessage(lineUserId, (String)lineMissionResponse.getBody());
                break;
            default:
                lineMissionResponse = executeCommand(lineUserId, lineMessage);
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
     * @param lineUserId 使用者ID
     * @param keyword 關鍵字
     * @return LineMissionResponse
     */
    public LineMissionResponse executeCommand(String lineUserId, String keyword) {
        // 依場站再分群組
        Map<String, List<StationBusDto>> stationBusDtoGroupByStationCode = groupByStationCode(lineUserId, keyword);
        List<BusDto> busDtoList = new ArrayList<>();
        WebClient.ResponseSpec data;

        // 再將各群組組成一個bus api query
        for (String station: stationBusDtoGroupByStationCode.keySet()) {
            List<StationBusDto> list = stationBusDtoGroupByStationCode.get(station);
            BusQueryRule busQueryRule = combineQuery(list);
            // 各自call BusAPIService
            val url = busAPIService.getBusEstimateTime(busQueryRule);
            log.info("url: {}", url);
            data = busAPIService.getData(url);
            List<BusDto> block = data.bodyToFlux(BusDto.class).collectList().block();
            System.out.println(block);
            busDtoList.addAll(block);
        }

        // 打包結果回傳
        val busDtoLineMissionResponse = new LineMissionResponse<List<BusDto>>();
        busDtoLineMissionResponse.setBody(busDtoList);

        List<String> messageList = busDtoList.stream()
                .map(bus -> String.format("%s：預估%d分%d秒到達%s",
                    bus.getRouteName().getZh_tw(),
                    bus.getEstimateTime() / 60,
                    bus.getEstimateTime() % 60,
                    bus.getStopName().getZh_tw()))
                .collect(Collectors.toList());

        log.info("info: {}", messageList);

        pushMessage(lineUserId, messageList);
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
        String collect = list.stream()
                .map(dto -> String.format("RouteName/Zh_tw eq '%s'", dto.getBusName()))
                .collect(Collectors.joining(" or "));
        rule.setFilter(collect);
        rule.setStationCode(list.get(0).getStationCode());
        return rule;
    }

    /**
     * 註冊關鍵字
     * @param lineUserId Line使用者ID
     * @param message 用戶訊息
     * @return
     */
    private LineMissionResponse registerCommand(String lineUserId, String message) {
        if (!userService.hasUser(lineUserId)) {
            userService.createUser(lineUserId);
        }

        val registerKeywordDTO = RegisterLineService.RegisterKeywordDTO.getDto(message);

        // 檢查該場站是否有多個方向
        List<StationGroup> stationGroups = stationGroupService.getStations(registerKeywordDTO.getStationName());
        val singleStation = 1;
        if (stationGroups.size() != singleStation) {
            // 將使用者指令暫存等後續知道要儲存哪個場站在取出
            redisService.set(lineUserId, message);

            String stationQueryMessage = queryStationMessage(stationGroups);
            pushMessage(lineUserId, stationQueryMessage);
            return new LineMissionResponse(SUCCESS, "需近一步確認場站方向");
        } else {
            // TODO 只有單一場站 極少數case
            return new LineMissionResponse(LineResponseCode.ERROR);
        }
    }

    private LineMissionResponse registerCommand(String lineUserId, String stationGroupId, boolean getStation) {
        String command = redisService.get(lineUserId);
        return registerLineService.registerCommand(lineUserId, command, stationGroupId);
    }

    /**
     * 詢問場站訊息
     * @param stationGroups
     * @return
     */
    public String queryStationMessage(List<StationGroup> stationGroups) {

        StringBuilder messageBuilder = new StringBuilder("--------------------------------\r\n" +
                "您所輸入的地點查詢有往多個方向，\r\n" +
                "請輸入您欲加入的場站編號 格式[場站] ${號碼}？\r\n");
        for (StationGroup stationGroup : stationGroups) {
            String direction;
            switch (stationGroup.getBearing()) {
                case "W":
                    direction = "(向西)";
                    break;
                case "E":
                    direction = "(向東)";
                    break;
                case "N":
                    direction = "(向北)";
                    break;
                case "S":
                    direction = "(向南)";
                    break;
                default:
                    direction = "";
            }
            String s = String.format("%d-%s %s\r\n", stationGroup.getId(), stationGroup.getStationName(), direction);
            messageBuilder.append(s);
        }
        messageBuilder.append("--------------------------------\r\n");
        return messageBuilder.toString();
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

    /**
     * 發送訊息給使用者
     * @param lineUserId 使用者ID
     * @param message 發送訊息
     * @return
     */
    public LineMissionResponse pushMessage(String lineUserId, String message) {
        log.info("lineUserId: {} message: {}", lineUserId, message);
        val textMessage = new TextMessage(message);
        lineMessagingClient.pushMessage(new PushMessage(lineUserId, textMessage));
        return new LineMissionResponse(SUCCESS);
    }

    public LineMissionResponse pushMessage(String lineUserId, List<String> message) {
        log.info("lineUserId: {} message: {}", lineUserId, message);
        List<Message> collect = message.stream().map(m -> new TextMessage(m)).collect(Collectors.toList());
        lineMessagingClient.pushMessage(new PushMessage(lineUserId, collect));
        return new LineMissionResponse(SUCCESS);
    }
}
