package com.kevin.linebotdemo.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

/**
 * @author liyanting
 */
@Service
@AllArgsConstructor
@Slf4j
public class RedisService {

    private final RedisTemplate<String, String> redisTemplate;

    public void set(String key, String value) {
        log.info(String.format("key: %s, value: %s 有效期限：五分鐘", key, value));
        redisTemplate.opsForValue().set(key, value, Duration.ofMinutes(5));
    }

    public String get(String key) {
        log.info("key: " + key);
        return redisTemplate.opsForValue().get(key);
    }

}
