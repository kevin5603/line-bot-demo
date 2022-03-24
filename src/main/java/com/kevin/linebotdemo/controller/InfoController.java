package com.kevin.linebotdemo.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 顯示一些基本資訊方便辨別雲端server程式版本
 * @author liyanting
 */
@RestController
@RequestMapping("api/v0")
public class InfoController {

    @Value("${info.version}")
    private String version;

    @GetMapping("version")
    public String showVersion() {
        return version;
    }
}
