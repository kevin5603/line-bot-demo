package com.kevin.linebotdemo.service;

import lombok.val;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

@SpringBootTest
@ActiveProfiles("test")
class RedisServiceTest {

    @Autowired
    private RedisService underTest;

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