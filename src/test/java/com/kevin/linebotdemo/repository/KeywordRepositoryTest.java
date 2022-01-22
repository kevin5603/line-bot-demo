package com.kevin.linebotdemo.repository;

import com.kevin.linebotdemo.model.Keyword;
import lombok.val;
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

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;

@ActiveProfiles("test")
@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = NONE)
class KeywordRepositoryTest {

    @Autowired
    private KeywordRepository underTest;

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
    public void shouldSaveKeyword() {
        // given
        val userId = UUID.randomUUID().toString();
        val word = "上班";
        val keyword = new Keyword(userId, word);

        // when
        underTest.save(keyword);

        // then
        List<Keyword> keywords = underTest.findByUserId(userId);
        assertEquals(1, keywords.size());
        assertEquals(userId, keywords.get(0).getUserId());
        assertEquals(word, keywords.get(0).getWord());


    }



}