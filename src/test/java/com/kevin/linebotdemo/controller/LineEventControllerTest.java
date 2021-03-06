package com.kevin.linebotdemo.controller;

import com.kevin.linebotdemo.model.BusQueryRule;
import com.kevin.linebotdemo.model.LineMissionResponse;
import com.kevin.linebotdemo.model.StationGroup;
import com.kevin.linebotdemo.model.dto.BusDto;
import com.kevin.linebotdemo.model.dto.NameType;
import com.kevin.linebotdemo.model.dto.StationBusDto;
import com.kevin.linebotdemo.service.*;
import com.kevin.linebotdemo.service.line.RegisterLineService;
import com.linecorp.bot.client.LineMessagingClient;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
class LineEventControllerTest {

    private LineEventController underTest;
    @MockBean
    private LineMessagingClient lineMessagingClient;
    @MockBean
    private RegisterLineService registerLineService;
    @MockBean
    private KeywordService keywordService;
    @MockBean
    private BusKeywordService busKeywordService;
    @MockBean
    private BusAPIService busAPIService;
    @MockBean
    private UserService userService;
    @MockBean
    private StationGroupService stationGroupService;
    @MockBean
    private RedisService redisService;

    @BeforeEach
    void setUp() {
        underTest = new LineEventController(
                lineMessagingClient,
                registerLineService,
                busKeywordService,
                busAPIService,
                userService,
                stationGroupService,
                redisService);
    }

    @Test
    void combineQuery() {
        // given
        List<StationBusDto> list = List.of(
                new StationBusDto("????????????", "000", "903"),
                new StationBusDto("????????????", "000", "645"),
                new StationBusDto("????????????", "000", "???32")
        );
        String expectFilter = "RouteName/Zh_tw eq '903' or RouteName/Zh_tw eq '645' or RouteName/Zh_tw eq '???32'";
        String expectSelect = "StopName,RouteName";

        // when
        BusQueryRule busQueryRule = underTest.combineQuery(list);

        // then
        assertEquals(expectSelect, busQueryRule.getSelect());
        assertEquals(expectFilter, busQueryRule.getFilter());
    }

    @Test
    void executeCommand() {
        // given
        String userId = UUID.randomUUID().toString();
        String keyword = "??????";
        val stationName = "????????????";
        val stationName2 = "?????????";
        val bus1 = "903";
        val bus2 = "645";
        val bus3 = "???32";
        val bus4 = "???36";
        List<StationBusDto> stationBusDtoList = List.of(
                new StationBusDto(stationName, "000", bus1),
                new StationBusDto(stationName, "000", bus2),
                new StationBusDto(stationName, "000", bus3),
                new StationBusDto(stationName2, "000", bus4));
        BusQueryRule busQueryRule = new BusQueryRule();
        val url = "url";
        val expectBusData1 = new BusDto();
        expectBusData1.setRouteName(new NameType(bus1, ""));
        val random = new Random(1).nextInt(600);
        expectBusData1.setEstimateTime(random);
        expectBusData1.setStopName(new NameType(stationName, ""));

        val expectBusData2 = new BusDto();
        expectBusData2.setRouteName(new NameType(bus2, ""));
        expectBusData2.setEstimateTime(random);
        expectBusData2.setStopName(new NameType(stationName, ""));

        val expectBusData3 = new BusDto();
        expectBusData3.setRouteName(new NameType(bus3, ""));
        expectBusData3.setEstimateTime(random);
        expectBusData3.setStopName(new NameType(stationName, ""));

        String expectResponse = "903?????????9???45?????????????????????\r\n" +
                "645?????????9???45?????????????????????\r\n" +
                "???32?????????9???45?????????????????????";


        Flux<BusDto> just = Flux.just(expectBusData1, expectBusData2, expectBusData3);

        when(busKeywordService.findByUserIdAndKeyword(userId, keyword)).thenReturn(stationBusDtoList);
        when(busAPIService.getBusEstimateTime(busQueryRule)).thenReturn(url);
        WebClient.ResponseSpec responseMock = mock(WebClient.ResponseSpec.class);
        when(busAPIService.getData(any())).thenReturn(responseMock);
        when(responseMock.bodyToFlux(BusDto.class)).thenReturn(just);

        // when
        LineMissionResponse<String> actualResponse = underTest.executeCommand(userId, keyword);

        // then
        assertEquals(expectResponse, actualResponse.getBody());
    }

    @Test
    void getStringListMap() {
        // given
        String userId = UUID.randomUUID().toString();
        String keyword = "??????";
        List<StationBusDto> stationBusDtoList = List.of(
                new StationBusDto("????????????", "000", "903"),
                new StationBusDto("????????????", "000", "645"),
                new StationBusDto("????????????", "000", "???32"),
                new StationBusDto("?????????", "999", "???36"));
        int expectSize = 2;
        when(busKeywordService.findByUserIdAndKeyword(userId, keyword)).thenReturn(stationBusDtoList);

        // when
        Map<String, List<StationBusDto>> stringListMap = underTest.groupByStationCode(userId, keyword);

        // then
        assertEquals(expectSize, stringListMap.size());
    }

    @Test
    void queryStationMessage() {
        // given
        val stationGroup1 = new StationGroup(1L, "????????????", "W");
        val stationGroup2 = new StationGroup(2L, "????????????", "E");
        val stationGroup3 = new StationGroup(3L, "????????????", "N");
        List<StationGroup> stationGroups = List.of(stationGroup1, stationGroup2, stationGroup3);

        // when
        String actualMessage = underTest.queryStationMessage(stationGroups);

        // then
        String expectMessage = "--------------------------------\r\n" +
                "????????????????????????????????????????????????\r\n" +
                "???????????????????????????????????? ??????[??????] ${??????}???\r\n" +
                "1-???????????? (??????)\r\n" +
                "2-???????????? (??????)\r\n" +
                "3-???????????? (??????)\r\n" +
                "--------------------------------\r\n";
        assertEquals(expectMessage, actualMessage);
    }
}