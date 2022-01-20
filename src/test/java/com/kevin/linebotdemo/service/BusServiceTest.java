package com.kevin.linebotdemo.service;

import com.kevin.linebotdemo.repository.BusKeywordRepository;
import com.kevin.linebotdemo.repository.BusRepository;
import com.kevin.linebotdemo.repository.StationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


class BusServiceTest {
    private BusService busService;
    private BusKeywordRepository busKeywordRepository =  Mockito.mock(BusKeywordRepository.class);
    private StationRepository stationRepository = Mockito.mock(StationRepository.class);
    private BusRepository busRepository = Mockito.mock(BusRepository.class);

    @BeforeEach
    void setUp() {
        busService = new BusService(busKeywordRepository,
                stationRepository,
                busRepository);
    }

    @Test
    void registerKeyword() {
        busService.registerKeyword("id", "註冊 回家 三民國中 630,617,645");
    }

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