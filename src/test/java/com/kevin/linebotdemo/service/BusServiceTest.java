package com.kevin.linebotdemo.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BusServiceTest {
    BusService s;

    @BeforeEach
    void setUp() {
        s = new BusService(null, null, null);
    }

    @Test
    void registerKeyword() {
        s.registerKeyword("註冊 回家 三民國中 630,617,645");
    }
}