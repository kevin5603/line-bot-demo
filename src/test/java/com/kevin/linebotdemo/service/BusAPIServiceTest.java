package com.kevin.linebotdemo.service;

import com.kevin.linebotdemo.model.BusQueryRule;
import com.kevin.linebotdemo.model.dto.BusDto;
import lombok.val;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("test")
@SpringBootTest
class BusAPIServiceTest {

    @Autowired
    private BusAPIService underTest;

    @Test
    void getBusEstimateTime() {
        // given

        // when
        val busQueryRule = new BusQueryRule("StopName,RouteName", "", "");
        String actualUrl = underTest.getBusEstimateTime(busQueryRule);
        String expectUrl = "https://ptx.transportdata.tw/MOTC/v2/Bus/EstimatedTimeOfArrival/City/Taipei/PassThrough/Station/%s?$select=StopName,RouteName&$format=JSON";
        // then
        assertEquals(expectUrl, actualUrl);
    }

    @Test
    @DisplayName("查詢到站時間抓取全部公車資訊")
    void getDataByBusEstimateTimeNoFilterCase() {
        // given
        val busQueryRule = new BusQueryRule("StopName,RouteName", "", "");
        var stationCode = "10";
        // when
        val busEstimateTime = String.format(underTest.getBusEstimateTime(busQueryRule), stationCode);
        System.out.println(busEstimateTime);
        List<BusDto> block = underTest.getData(busEstimateTime)
                .bodyToFlux(BusDto.class)
                .log()
                .collectList()
                .block();
        // then
        assertEquals(4, block.size());
    }

    @Test
    @DisplayName("查詢到站時間抓取特定公車資訊")
    void getDataByBusEstimateTimeUseFilterCase() {
        // given
        val busQueryRule = new BusQueryRule("StopName,RouteName", "RouteName/Zh_tw eq '308' or RouteName/Zh_tw eq '756'", "");
        var stationCode = "10";
        // when
        val busEstimateTime = String.format(underTest.getBusEstimateTime(busQueryRule), stationCode);
        System.out.println(busEstimateTime);
        List<BusDto> block = underTest.getData(busEstimateTime)
                .bodyToFlux(BusDto.class)
                .log()
                .collectList()
                .block();
        // then
        assertEquals(2, block.size());
    }
}
