package com.kevin.linebotdemo.repository;

import com.kevin.linebotdemo.model.Station;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;

@ActiveProfiles("test")
@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = NONE)
class StationRepositoryTest {

    @Autowired
    private StationRepository underTest;

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
    void findByNameAndBearing() {
        // given

        // when

        // then
    }

    @Test
    void findIdByNameAndBearing() {
        // given
        val stationName = "三民國中";
        val west = "W";
        val station1 = new Station(1L, "0001", stationName, "三民地址", west);
        val station2 = new Station(2L, "0002", stationName, "三民地址2", "E");
        val station3 = new Station(3L, "0003", "上灣仔", "上灣仔地址", west);
        val station4 = new Station(4L, "0004", "上灣仔", "上灣仔地址", "E");
        val station5 = new Station(5L, "0005", stationName, "三民地址3", west);
        underTest.saveAll(List.of(station1, station2, station3, station4, station5));

        // when
        List<Long> actualIdList = underTest.findIdByNameAndBearing(stationName, west);

        // then
        assertEquals(2, actualIdList.size());
    }
}