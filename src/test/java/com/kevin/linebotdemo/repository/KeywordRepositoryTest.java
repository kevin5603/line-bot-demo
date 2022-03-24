package com.kevin.linebotdemo.repository;

import com.kevin.linebotdemo.config.PostgresContainerExtension;
import com.kevin.linebotdemo.model.Keyword;
import lombok.val;
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
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;

@ExtendWith(PostgresContainerExtension.class)
@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = NONE)
class KeywordRepositoryTest {

    @Autowired
    private KeywordRepository underTest;

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