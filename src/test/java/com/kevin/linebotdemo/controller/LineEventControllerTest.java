package com.kevin.linebotdemo.controller;

import com.kevin.linebotdemo.model.BusQueryRule;
import com.kevin.linebotdemo.model.LineMissionResponse;
import com.kevin.linebotdemo.model.StationGroup;
import com.kevin.linebotdemo.model.dto.BusDto;
import com.kevin.linebotdemo.model.dto.StationBusDto;
import com.kevin.linebotdemo.service.BusAPIService;
import com.kevin.linebotdemo.service.BusKeywordService;
import com.kevin.linebotdemo.service.KeywordService;
import com.kevin.linebotdemo.service.UserService;
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

    @BeforeEach
    void setUp() {
        underTest = new LineEventController(
                lineMessagingClient,
                registerLineService,
                keywordService,
                busKeywordService,
                busAPIService,
                userService, null, null);
    }

    @Test
    void combineQuery() {
        // given
        List<StationBusDto> list = List.of(
                new StationBusDto("三民國中", "000", "903"),
                new StationBusDto("三民國中", "000", "645"),
                new StationBusDto("三民國中", "000", "紅32")
        );
        String expectFilter = "RouteName/Zh_tw eq '903' or RouteName/Zh_tw eq '645' or RouteName/Zh_tw eq '紅32'";
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
        String keyword = "上班";
        List<StationBusDto> stationBusDtoList = List.of(
                new StationBusDto("三民國中", "000", "903"),
                new StationBusDto("三民國中", "000", "645"),
                new StationBusDto("三民國中", "000", "紅32"),
                new StationBusDto("上灣仔", "000", "藍36"));
        BusQueryRule busQueryRule = new BusQueryRule();
        val url = "url";
        Flux<BusDto> just = Flux.just(new BusDto());
        List<BusDto> expectResponse = just.collectList().block();

        when(busKeywordService.findByUserIdAndKeyword(userId, keyword)).thenReturn(stationBusDtoList);
        when(busAPIService.getBusEstimateTime(busQueryRule)).thenReturn(url);
        WebClient.ResponseSpec responseMock = mock(WebClient.ResponseSpec.class);
        when(busAPIService.getData(any())).thenReturn(responseMock);
        when(responseMock.bodyToFlux(BusDto.class)).thenReturn(just);

        // when
        LineMissionResponse actualResponse = underTest.executeCommand(userId, keyword);

        // then
        assertEquals(expectResponse, actualResponse.getBody());
    }

    @Test
    void getStringListMap() {
        // given
        String userId = UUID.randomUUID().toString();
        String keyword = "上班";
        List<StationBusDto> stationBusDtoList = List.of(
                new StationBusDto("三民國中", "000", "903"),
                new StationBusDto("三民國中", "000", "645"),
                new StationBusDto("三民國中", "000", "紅32"),
                new StationBusDto("上灣仔", "999", "藍36"));
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
        val stationGroup1 = new StationGroup(1L, "三民國中", "W");
        val stationGroup2 = new StationGroup(2L, "三民國中", "E");
        val stationGroup3 = new StationGroup(3L, "三民國中", "N");
        List<StationGroup> stationGroups = List.of(stationGroup1, stationGroup2, stationGroup3);

        // when
        String actualMessage = underTest.queryStationMessage(stationGroups);

        // then
        String expectMessage = "--------------------------------\r\n" +
                "您所輸入的地點查詢有往多個方向，\r\n" +
                "請輸入您欲加入的場站編號 格式[場站] ${號碼}？\r\n" +
                "1-三民國中 (向西)\r\n" +
                "2-三民國中 (向東)\r\n" +
                "3-三民國中 (向北)\r\n" +
                "--------------------------------\r\n";
        assertEquals(expectMessage, actualMessage);
    }
}