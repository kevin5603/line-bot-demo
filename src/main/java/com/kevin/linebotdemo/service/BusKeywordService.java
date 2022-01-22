package com.kevin.linebotdemo.service;

import com.kevin.linebotdemo.model.BusKeyword;
import com.kevin.linebotdemo.model.dto.StationBusDto;
import com.kevin.linebotdemo.repository.BusKeywordRepository;
import lombok.AllArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author liyanting
 */
@Service
@AllArgsConstructor
public class BusKeywordService {

    private final BusKeywordRepository busKeywordRepository;

    @Transactional(rollbackFor = Exception.class)
    public void save(Long keywordId, Long stationGroupId, Long busId) {
        val busKeyword = new BusKeyword(keywordId, stationGroupId, busId);
        busKeywordRepository.save(busKeyword);
    }

    public List<StationBusDto> findByUserIdAndKeyword(String userId, String keyword) {
        return busKeywordRepository.findByUserIdAndKeyword(userId, keyword);
    }
}
