package com.kevin.linebotdemo.service;

import com.kevin.linebotdemo.model.Keyword;
import com.kevin.linebotdemo.repository.BusKeywordRepository;
import com.kevin.linebotdemo.repository.KeywordRepository;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(SpringExtension.class)
class KeywordServiceTest {

    private KeywordService underTest;
    @MockBean
    private KeywordRepository keywordRepository;
    @MockBean
    private BusKeywordRepository busKeywordRepository;

    @BeforeEach
    void setUp() {
        underTest = new KeywordService(keywordRepository, busKeywordRepository);
    }

    @Test
    void deleteByUserIdAndWordIfFoundKeyword() {
        // given
        val userId = UUID.randomUUID().toString();
        val word = "回家";
        val keyword = new Keyword(1L, userId, word);
        when(keywordRepository.findByUserIdAndWord(userId, word))
                .thenReturn(Optional.of(keyword));

        // when
        underTest.deleteByUserIdAndWord(userId, word);

        // then
        verify(keywordRepository, times(1)).delete(keyword);
        verify(busKeywordRepository, times(1))
                .deleteByKeywordId(keyword.getId());
    }

    @Test
    void deleteByUserIdAndWordIfNotFoundKeyword() {
        // given
        val userId = UUID.randomUUID().toString();
        val word = "回家";
        val keyword = new Keyword(1L, userId, word);
        when(keywordRepository.findByUserIdAndWord(userId, word))
                .thenReturn(Optional.empty());

        // when
        underTest.deleteByUserIdAndWord(userId, word);

        // then
        verify(keywordRepository, never()).delete(keyword);
        verify(busKeywordRepository, never())
                .deleteByKeywordId(keyword.getId());
    }


    @Test
    void getKeywordIdIfKeywordExists() {
        // given
        val userId = UUID.randomUUID().toString();
        val word = "回家";
        val keyword = new Keyword(1L, userId, word);
        when(keywordRepository.findByUserIdAndWord(userId, word))
                .thenReturn(Optional.of(keyword));
        // when
        Long actualKeywordId = underTest.getKeywordId(userId, word);

        // then
        assertEquals(keyword.getId(), actualKeywordId);
        verify(keywordRepository, times(1)).findByUserIdAndWord(userId, word);
        verify(keywordRepository, never()).save(any());
    }

    @Test
    void getKeywordIdIfKeywordNotExists() {
        // given
        val userId = UUID.randomUUID().toString();
        val word = "回家";
        when(keywordRepository.findByUserIdAndWord(userId, word))
                .thenReturn(Optional.empty());
        val expectKeywordId = 1L;
        when(keywordRepository.save(new Keyword(userId, word)))
                .thenReturn(new Keyword(expectKeywordId, userId, word));
        // when
        Long actualKeywordId = underTest.getKeywordId(userId, word);

        // then
        assertEquals(expectKeywordId, actualKeywordId);
        verify(keywordRepository, times(1)).findByUserIdAndWord(userId, word);
        verify(keywordRepository, times(1)).save(any());
    }

    @Test
    void registerKeyword() {
        // given

        // when

        // then
    }
}