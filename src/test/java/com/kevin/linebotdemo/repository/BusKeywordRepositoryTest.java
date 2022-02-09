package com.kevin.linebotdemo.repository;

import com.kevin.linebotdemo.model.Bus;
import com.kevin.linebotdemo.model.BusKeyword;
import com.kevin.linebotdemo.model.Keyword;
import com.kevin.linebotdemo.model.Station;
import com.kevin.linebotdemo.model.dto.BusKeywordInfoDto;
import com.kevin.linebotdemo.model.dto.StationBusDto;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.*;

@ActiveProfiles("test")
@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = NONE)
class BusKeywordRepositoryTest {

    @Autowired
    private BusKeywordRepository underTest;
    @Autowired
    private BusRepository busRepository;
    @Autowired
    private KeywordRepository keywordRepository;
    @Autowired
    private StationRepository stationRepository;

    @Container
    public static PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer("postgres:11.1")
            .withDatabaseName("test-db")
            .withUsername("sa")
            .withPassword("Passw0rd");

    @DynamicPropertySource
    public static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
    }



    @Test
    void findByUserIdAndKeyword() {
        // given
        val userId = UUID.randomUUID().toString();
        val word = "回家";
        findAllKeywordInfoInitData(userId);

        // when
        val infoByKeywordId = underTest.findByUserIdAndKeyword(userId, word);

        // then
        assertEquals(3, infoByKeywordId.size());
    }

    @Test
    void deleteByUserIdAndKeyword() {
        // give
        val userId = UUID.randomUUID().toString();
        val keywordId = 1L;
        val stationId = 1L;
        val busId = 1L;
        val busKeyword = new BusKeyword(keywordId, stationId, busId);
        underTest.save(busKeyword);

        // when
        underTest.deleteByKeywordId(keywordId);

        // then
        List<BusKeyword> expectList = underTest.findAll();

        assertEquals(0, expectList.size());
    }

    @Test
    void findAllKeywordInfoByUserId() {
        // given
        var user_id = UUID.randomUUID().toString();
        List<BusKeywordInfoDto> expectBusKeywordInfo = findAllKeywordInfoInitData(user_id);

        // when
        List<BusKeywordInfoDto> actualBusKeywordInfo = underTest.findAllKeywordInfoByUserId(user_id);

        // then
        Assertions.assertThat(actualBusKeywordInfo).hasSameElementsAs(expectBusKeywordInfo);

    }

    private List<BusKeywordInfoDto> findAllKeywordInfoInitData(String user_id) {
        val bus1 = new Bus("630");
        val bus2 = new Bus("617");
        val bus3 = new Bus("645");
        val bus4 = new Bus("藍36");
        busRepository.save(bus1);
        busRepository.save(bus2);
        busRepository.save(bus3);
        busRepository.save(bus4);
        val busId1 = busRepository.findByName(bus1.getName()).get();
        val busId2 = busRepository.findByName(bus2.getName()).get();
        val busId3 = busRepository.findByName(bus3.getName()).get();
        val busId4 = busRepository.findByName(bus4.getName()).get();

        val station1 = new Station("999", "三民國中", "三民國中(向西)", "W");
        val station2 = new Station("000", "上灣仔", "鬍鬚張(向北)", "E");
        stationRepository.save(station1);
        stationRepository.save(station2);
        Long stationId1 = stationRepository.findIdByNameAndBearing(station1.getName(), station1.getBearing()).get(0);
        Long stationId2 = stationRepository.findIdByNameAndBearing(station2.getName(), station2.getBearing()).get(0);

        val word = "回家";
        val word2 = "上班";
        val keyword = new Keyword(user_id, word);
        val keyword2 = new Keyword(user_id, word2);
        keywordRepository.save(keyword);
        keywordRepository.save(keyword2);
        Keyword actualKeyword = keywordRepository.findByUserIdAndWord(user_id, word).get();
        Keyword actualKeyword2 = keywordRepository.findByUserIdAndWord(user_id, word2).get();

        underTest.save(new BusKeyword(actualKeyword.getId(), stationId1, busId2.getId()));
        underTest.save(new BusKeyword(actualKeyword.getId(), stationId1, busId3.getId()));
        underTest.save(new BusKeyword(actualKeyword.getId(), stationId2, busId4.getId()));
        underTest.save(new BusKeyword(actualKeyword2.getId(), stationId1, busId1.getId()));

        val expectInfo1 = new BusKeywordInfoDto(keyword.getWord(), station1.getName(), bus2.getName());
        val expectInfo2 = new BusKeywordInfoDto(keyword.getWord(), station1.getName(), bus3.getName());
        val expectInfo3 = new BusKeywordInfoDto(keyword.getWord(), station2.getName(), bus4.getName());
        val expectInfo4 = new BusKeywordInfoDto(keyword2.getWord(), station1.getName(), bus1.getName());
        return List.of(expectInfo1, expectInfo2, expectInfo3, expectInfo4);
    }


    @Test
    void deleteByUserIdAndKeyWord() {
        // given
        val word = "回家";
        val userId = UUID.randomUUID().toString();
        findAllKeywordInfoInitData(userId);

        // when
        underTest.deleteByUserIdAndKeyWord(userId, word);

        // then
        assertEquals(1, underTest.findAll().size());
    }

    @Test
    void deleteByUserIdAndKeyWordAndBusId() {
        // given
        val word = "回家";
        val userId = UUID.randomUUID().toString();
        val busList = List.of("645", "617");
        findAllKeywordInfoInitData(userId);

        // when
        underTest.deleteByUserIdAndKeyWordAndBusId(userId, word, busList);

        // then
        assertEquals(2, underTest.findAll().size());
    }
}