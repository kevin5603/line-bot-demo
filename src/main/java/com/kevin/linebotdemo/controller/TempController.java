package com.kevin.linebotdemo.controller;

import com.kevin.linebotdemo.service.BusService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author liyanting
 */
@RestController
@AllArgsConstructor
@Slf4j
public class TempController {

    private final BusService busService;

    @GetMapping("/api/v1")
    public void a() {
//        log.info(" ========== test ==========");
//        busService.registerKeyword("kevin5603", "註冊 回家 三民國中 630,617,645");
//        log.info(" ========== test ==========");
    }
}
