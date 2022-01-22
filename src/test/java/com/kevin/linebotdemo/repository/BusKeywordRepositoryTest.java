package com.kevin.linebotdemo.repository;

import com.kevin.linebotdemo.model.Bus;
import com.kevin.linebotdemo.model.BusKeyword;
import com.kevin.linebotdemo.model.Keyword;
import com.kevin.linebotdemo.model.Station;
import com.kevin.linebotdemo.model.dto.StationBusDto;
import lombok.val;
import org.junit.ClassRule;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
    private BusKeywordRepository busKeywordRepository;
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
        val bus1 = new Bus("630");
        val bus2 = new Bus("617");
        val bus3 = new Bus("645");
        val bus4 = new Bus("藍36");
        busRepository.save(bus1);
        busRepository.save(bus2);
        busRepository.save(bus3);
        busRepository.save(bus4);

        val station1 = new Station(1L, "999", "三民國中", "三民國中(向西)", "W");
        val station2 = new Station(2L, "000", "上灣仔", "鬍鬚張(向北)", "E");
        stationRepository.save(station1);
        stationRepository.save(station2);

        val user_id = UUID.randomUUID().toString();
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

    @Test
    void deleteByUserIdAndKeyword() {
        // give
        val userId = UUID.randomUUID().toString();
        val keywordId = 1L;
        val stationId = 1L;
        val busId = 1L;
        val busKeyword = new BusKeyword(keywordId, stationId, busId);
        busKeywordRepository.save(busKeyword);

        // when
        busKeywordRepository.deleteByKeywordId(keywordId);

        // then
        List<BusKeyword> expectList = busKeywordRepository.findAll();

        assertEquals(0, expectList.size());

    }

}