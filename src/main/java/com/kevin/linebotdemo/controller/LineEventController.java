package com.kevin.linebotdemo.controller;


import com.kevin.linebotdemo.model.LineMissionRequest;
import com.kevin.linebotdemo.model.LineMissionResponse;
import com.kevin.linebotdemo.service.RegisterLineService;
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

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * 監聽Line事件，當事件發生依需求傳遞給不同service處理後續事項
 *
 * @author liyanting
 */
@Slf4j
@LineMessageHandler
@AllArgsConstructor
public class LineEventController {

    private final LineMessagingClient lineMessagingClient;
    private final RegisterLineService registerLineService;

    // TODO
    public static void main(String[] args) {
        String s = "註冊 回家 三民國中 630,617,645";
        int i = s.indexOf(" ");
        String substring = s.substring(i + 1);
        System.out.println(i);
        System.out.println(substring);

    }

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

        switch (headString) {
            case "註冊":
                lineMissionResponse = registerCommand(userId, lineMessage);
                break;
            default:
                lineMissionResponse = executeCommand(lineMessage);
        }
        return lineMissionResponse;
    }

    /**
     * 擷取最前面字段來判斷要做哪個動作
     *
     * @param lineMessage line訊息
     * @return
     */
    private String getCommand(String lineMessage) {
        return lineMessage.split(" ")[0];
    }

    /**
     * 執行指令
     *
     * @param lineMessage
     * @return
     */
    private LineMissionResponse executeCommand(String lineMessage) {
        return null;
    }

    /**
     * 註冊關鍵字
     * @param userId Line使用者ID
     * @param lineMessage 用戶訊息
     * @return
     */
    private LineMissionResponse registerCommand(String userId, String lineMessage) {
        String message = filterCommand(lineMessage);
        return registerLineService.registerCommand(userId, message);
    }

    /**
     * 過濾開頭指令
     * @param lineMessage
     * @return
     */
    private String filterCommand(String lineMessage) {
        return lineMessage.substring(lineMessage.indexOf(" ") + 1);
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
