package com.kevin.linebotdemo.repository;

import com.kevin.linebotdemo.model.Bus;
import com.kevin.linebotdemo.model.BusKeyword;
import com.kevin.linebotdemo.model.Keyword;
import com.kevin.linebotdemo.model.Station;
import com.kevin.linebotdemo.model.dto.StationBusDto;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

@DataJpaTest
class BusKeywordRepositoryTest {

    @Autowired
    private BusKeywordRepository busKeywordRepository;
    @Autowired
    private BusRepository busRepository;
    @Autowired
    private KeywordRepository keywordRepository;
    @Autowired
    private StationRepository stationRepository;

    @Test
        // TODO
    void findByUserId() {
        // give
        val user_id = "kevin5603";
        val keyword = "回家";
        val station_id = 1L;
        val busId = 1L;
//        val busKeyword = new BusKeyword(keyword, station_id, busId);
//        busKeywordRepository.save(busKeyword);

        // when
//        List<BusKeyword> byUserId = busKeywordRepository.findByUserId(user_id);

        // then
//        assertEquals(1, byUserId.size());
//
//        assertThat(byUserId)
//                .usingRecursiveComparison()
//                .isEqualTo(List.of(busKeyword));
    }

    @Test
    // TODO
    void findByUserIdAndKeyword() {
        // give
        val user_id = "kevin5603";
        val keyword = "回家";
        val station_id = 1L;
        val busId = 1L;
        List<BusKeyword> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
//            val busKeyword = new BusKeyword(keyword, station_id, busId + i);
//            list.add(busKeyword);
//            busKeywordRepository.save(busKeyword);
        }

        // when
//        List<BusKeyword> byUserIdAndKeyword = busKeywordRepository.findByUserIdAndKeyword(user_id, keyword);

        // then
//        assertEquals(10, byUserIdAndKeyword.size());
//
//        assertThat(byUserIdAndKeyword)
//                .usingRecursiveComparison()
//                .isEqualTo(List.of(list));
    }

    @Test
    void deleteByUserIdAndKeyword() {
        // give
        val user_id = "kevin5603";
        val keyword = "回家";
        val station_id = 1L;
        val busId = 1L;
//        val busKeyword = new BusKeyword(keyword, station_id, busId);

        // when
//        busKeywordRepository.save(busKeyword);

        // then
    }

    @Test
    void findInfoByKeywordId() {
        // given
        val bus1 = new Bus(1L, "630");
        val bus2 = new Bus(2L, "617");
        val bus3 = new Bus(3L, "645");
        val bus4 = new Bus(4L, "藍36");
        busRepository.save(bus1);
        busRepository.save(bus2);
        busRepository.save(bus3);
        busRepository.save(bus4);

        val station1 = new Station(1L, "999", "三民國中", "三民國中(向西)");
        val station2 = new Station(2L, "000", "上灣仔", "鬍鬚張(向北)");
        stationRepository.save(station1);
        stationRepository.save(station2);

        val user_id = "kevin5603";
        val word = "回家";
        val keyword = new Keyword(1L, user_id, word);
        keywordRepository.save(keyword);
        busKeywordRepository.save(new BusKeyword(1L, 1L, 2L));
        busKeywordRepository.save(new BusKeyword(1L, 1L, 3L));
        busKeywordRepository.save(new BusKeyword(1L, 2L, 4L));

        // when
        val infoByKeywordId = busKeywordRepository.findByUserIdAndKeyword(user_id, word);

        // then
        val sb1 = new StationBusDto(station1.getName(), station1.getCode(), bus2.getName());
        val sb2 = new StationBusDto(station1.getName(), station1.getCode(), bus3.getName());
        val sb3 = new StationBusDto(station2.getName(), station2.getCode(), bus4.getName());
        List<StationBusDto> stationBusDtos = List.of(sb1, sb2, sb3);
        assertEquals(3, infoByKeywordId.size());
        assertThat(infoByKeywordId).usingRecursiveComparison()
                .isEqualTo(stationBusDtos);
    }
}