package com.kevin.linebotdemo.controller;

import com.kevin.linebotdemo.model.StationGroup;
import com.kevin.linebotdemo.model.dto.NameType;
import com.kevin.linebotdemo.model.dto.StationDto;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InitControllerTest {

    InitController underTest;

//    @BeforeEach
    public void setUP() {
//        underTest = new InitController(null, null, null, null);
    }

//    @Test
    void saveAllStationGroup() {
        // given
        List<StationDto> stationDtoList = List.of(
                new StationDto("1L", new NameType("三民國中", "sam"), "台北市", null, "W"),
                new StationDto("3L", new NameType("三民國中", "sam"), "台北市", null, "W"),
                new StationDto("4L", new NameType("三民國中", "sam"), "台北市", null, "E"),
                new StationDto("2L", new NameType("新湖國小", "sam"), "台北市", null, "E")
        );
        // when
        underTest.saveAllStationGroup(stationDtoList);
        // then
    }
}