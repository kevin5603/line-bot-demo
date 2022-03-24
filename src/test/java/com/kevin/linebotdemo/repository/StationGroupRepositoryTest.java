package com.kevin.linebotdemo.repository;

import com.kevin.linebotdemo.config.PostgresContainerExtension;
import com.kevin.linebotdemo.model.StationGroup;
import lombok.val;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;

@ExtendWith(PostgresContainerExtension.class)
@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = NONE)
class StationGroupRepositoryTest {

    @Autowired
    private StationGroupRepository underTest;

    @Test
    void countByStationName() {
        // given
        val stationName = "三民國中";
        val stationName2 = "上灣仔";
        val s1 = new StationGroup(1L, stationName, "W");
        val s2 = new StationGroup(2L, stationName, "E");
        val s3 = new StationGroup(3L, stationName2, "E");
        underTest.saveAll(List.of(s1, s2, s3));

        // when
//        Long actualCount = underTest.countByStationName(stationName);

        // then
//        assertEquals(2, actualCount);
    }
}