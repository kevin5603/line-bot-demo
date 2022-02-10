package com.kevin.linebotdemo.service;

import lombok.val;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import redis.embedded.RedisServer;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
class RedisServiceTest {

    @Autowired
    private RedisService underTest;

    private static RedisServer redisServer;


    @BeforeAll
    static void startRedis() {
        redisServer = RedisServer.builder()
                .port(6379)
                .setting("maxmemory 128M") //maxheap 128M
                .build();
        redisServer.start();
    }

    @AfterAll
    static void stopRedis() {
        redisServer.stop();
    }

    @Test
    void set() {
        // given
        val name = "kevin";

        // when
        val key = "name";
        underTest.set(key, name);

        // then
        assertEquals(name, underTest.get(key));
    }

    @Test
    void get() {
        // given

        // when

        // then
    }
}