package com.kevin.linebotdemo.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author liyanting
 */
//@Component
//@ConfigurationProperties(prefix = "bus")
//@Data
public class LineBotConfig {
    private String appId;
    private String appKey;

}
