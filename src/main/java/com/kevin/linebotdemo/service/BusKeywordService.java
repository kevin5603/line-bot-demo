package com.kevin.linebotdemo.service;

import com.kevin.linebotdemo.model.BusKeyword;
import com.kevin.linebotdemo.repository.BusKeywordRepository;
import lombok.AllArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author liyanting
 */
@Service
@AllArgsConstructor
public class BusKeywordService {

    private final BusKeywordRepository busKeywordRepository;

    @Transactional(rollbackFor = Exception.class)
    public void save(Long keywordId, Long stationId, Long busId) {
        val busKeyword = new BusKeyword(keywordId, stationId, busId);
        busKeywordRepository.save(busKeyword);
    }
}
