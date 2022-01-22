package com.kevin.linebotdemo.service;

import com.kevin.linebotdemo.repository.BusKeywordRepository;
import com.kevin.linebotdemo.repository.BusRepository;
import com.kevin.linebotdemo.repository.KeywordRepository;
import com.kevin.linebotdemo.repository.StationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


class BusServiceTest {
    private BusService busService;
    private BusKeywordRepository busKeywordRepository =  Mockito.mock(BusKeywordRepository.class);
    private KeywordRepository keywordRepository = Mockito.mock(KeywordRepository.class);
    private StationRepository stationRepository = Mockito.mock(StationRepository.class);
    private BusRepository busRepository = Mockito.mock(BusRepository.class);

    @BeforeEach
    void setUp() {
        busService = new BusService(busKeywordRepository,
                keywordRepository,
                stationRepository,
                busRepository);
    }

//    @Test
    // TODO
//    void registerKeyword() {
//        // give
//        val user_id = "kevin5603";
//        val keyword = "回家";
//        val station_id = 1L;
//        val busId = 1L;
//        val busKeyword = new BusKeyword(keyword, station_id, busId);
//        when(busKeywordRepository.save(any())).thenReturn(busKeyword);
//        val stationName = "三民國中";
//        val stationCode = "123";
//        val address = "三民國中(向西)";
//        val station = new Station(station_id, stationCode, stationName, address);
//        when(stationRepository.findByName(stationName)).thenReturn(Optional.of(station));
//        val busName = "630";
//        val bus = new Bus(busId, busName);
//        when(busRepository.findByName(busName)).thenReturn(Optional.of(bus));
//
//        // when
//        busService.registerKeyword(user_id, "註冊 回家 三民國中 630");
//
//        // then
//        then(busKeywordRepository).should(new Times(1)).save(busKeyword);
//
//    }

    @Test
    void testRegisterKeyword() {
    }

    @Test
    void getStationId() {
    }

    @Test
    void saveBus() {
    }

    @Test
    void getBusId() {
    }

    @Test
    void findBusKeywordsByUserId() {
    }

    @Test
    void deleteByUserIdAndKeywordId() {
    }
}