package com.kevin.linebotdemo.service;

import com.kevin.linebotdemo.model.Keyword;
import com.kevin.linebotdemo.repository.BusKeywordRepository;
import com.kevin.linebotdemo.repository.KeywordRepository;
import lombok.AllArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 處理關鍵字
 * @author liyanting
 */
@Service
@AllArgsConstructor
public class KeywordService {
    private final KeywordRepository keywordRepository;
    private final BusKeywordRepository busKeywordRepository;

    @Transactional(readOnly = true)
    public List<Keyword> findKeywordByUserId(String userId) {
        return keywordRepository.findByUserId(userId);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteByUserIdAndWord(String userId, String word) {
        Optional<Keyword> byUserIdAndWord = keywordRepository.findByUserIdAndWord(userId, word);
        if (byUserIdAndWord.isPresent()) {
            val keyword = byUserIdAndWord.get();
            keywordRepository.delete(keyword);
            busKeywordRepository.deleteByKeywordId(keyword.getId());
        }
    }

    public Optional<Keyword> findByUserIdAndWord(String userId, String keyword) {
        return keywordRepository.findByUserIdAndWord(userId, keyword);
    }

    @Transactional(rollbackFor = Exception.class)
    public Keyword registerKeyword(String userId, String keyword) {
        val entity = new Keyword(userId, keyword);
        return keywordRepository.save(entity);
    }

    public Long getKeywordId(String userId, String keyword) {
        val optionalKeyword = findByUserIdAndWord(userId, keyword);
        if (optionalKeyword.isPresent()) {
            return optionalKeyword.get().getId();
        } else {
            val entity = registerKeyword(userId, keyword);
            return entity.getId();
        }
    }
}
