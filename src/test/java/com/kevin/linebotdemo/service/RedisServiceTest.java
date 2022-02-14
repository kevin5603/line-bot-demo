package com.kevin.linebotdemo.service;

import com.kevin.linebotdemo.config.RedisContainerExtension;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(RedisContainerExtension.class)
@ActiveProfiles("test")
@SpringBootTest
class RedisServiceTest {

    @Autowired
    private RedisService redisService;

    @Test
    void testGetAndSet() {
        // given
        val key = "name";
        val expectValue = "kevin.li";

        // when
        redisService.set(key, expectValue);

        // then
        String actualValue = redisService.get(key);
        assertEquals(expectValue, actualValue);
    }

}