package com.kevin.linebotdemo.schedu;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class BusSchedule {
    int i = 0;

    @Scheduled(cron = "0 */1 22-23 * * *")
    public void query() {
        log.info("hello:" + i++);

    }
}
