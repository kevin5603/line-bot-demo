package com.kevin.linebotdemo.controller;

import com.kevin.linebotdemo.model.Student;
import com.linecorp.bot.client.LineMessagingClient;
import com.linecorp.bot.model.Broadcast;
import com.linecorp.bot.model.message.Message;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.model.response.BotApiResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * call api 發送資訊到line上
 *
 * @author liyanting
 */
@RestController
@RequestMapping("api/v1/message")
@AllArgsConstructor
@Slf4j
public class MessageController {

    LineMessagingClient lineMessagingClient;

    @PostMapping("send")
    public void sendMessage(@RequestBody Student student) {
        log.info("", student);
        log.info(student.toString());
        broadcast(student);
    }

    public void broadcast(Student student) {
        List<Message> textMessages = List.of(new TextMessage(student.toString()));
        Broadcast broadcast = new Broadcast(textMessages);
        lineMessagingClient.broadcast(broadcast);
    }
}

