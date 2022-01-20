package com.kevin.linebotdemo;

import com.linecorp.bot.spring.boot.annotation.LineMessageHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author liyanting
 */
@SpringBootApplication
@LineMessageHandler
@Slf4j
@EnableScheduling
public class LineBotDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(LineBotDemoApplication.class, args);
    }


}
