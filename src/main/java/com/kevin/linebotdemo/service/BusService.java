package com.kevin.linebotdemo.service;

import com.kevin.linebotdemo.exception.KeywordNotFoundException;
import com.kevin.linebotdemo.model.Bus;
import com.kevin.linebotdemo.model.BusKeyword;
import com.kevin.linebotdemo.model.Keyword;
import com.kevin.linebotdemo.repository.BusKeywordRepository;
import com.kevin.linebotdemo.repository.BusRepository;
import com.kevin.linebotdemo.repository.KeywordRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author liyanting
 */
@Service
@AllArgsConstructor
@Slf4j
public class BusService {
    private final BusKeywordRepository busKeywordRepository;
    private final KeywordRepository keywordRepository;
    private final BusRepository busRepository;


    /**
     * @param cmd
     */
    @Transactional(rollbackFor = Exception.class)
    public void registerKeyword(String cmd) {
        String[] s = cmd.split(" ");
        String keyword = s[1];
        String station = s[2];
        String[] busList = s[3].split(",");
        log.info(keyword);
        log.info(station);
        for(String ss: busList) {
            log.info(ss);
        }

    }

    @Transactional(rollbackFor = Exception.class)
    public Bus saveBus(String busName) {
        return busRepository.save(new Bus(busName));
    }

    public Long getBusId(String busName) {
        return busRepository.findByName(busName)
                .orElse(saveBus(busName)).getId();
    }

    /**
     * @param userId
     * @return
     */
    @Transactional(readOnly = true)
    List<BusKeyword> findBusKeywordsByUserId(String userId) {
        return busKeywordRepository.findBusKeywordsByUserId(userId);
    }

    @Transactional(rollbackFor = Exception.class)
    void deleteByUserIdAndKeywordId(String userId, String keyword) {
        Keyword key = keywordRepository.findByWord(keyword)
                .orElseThrow(() -> new KeywordNotFoundException("沒有找到關鍵字"));
        busKeywordRepository.deleteByUserIdAndKeywordId(userId, key.getId());
    }
}
