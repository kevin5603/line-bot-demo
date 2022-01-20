package com.kevin.linebotdemo.service;

import com.kevin.linebotdemo.model.Bus;
import com.kevin.linebotdemo.model.BusKeyword;
import com.kevin.linebotdemo.model.Station;
import com.kevin.linebotdemo.repository.BusKeywordRepository;
import com.kevin.linebotdemo.repository.BusRepository;
import com.kevin.linebotdemo.repository.StationRepository;
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
    private final StationRepository stationRepository;
    private final BusRepository busRepository;


    /**
     * @param lineMessage
     */
    @Transactional(rollbackFor = Exception.class)
    public void registerKeyword(String userId, String lineMessage) {
        String[] s = lineMessage.split(" ");
        String keyword = s[1];
        String station = s[2];
        String[] busList = s[3].split(",");

        Long stationId = getStationId(station);

        for(String bus: busList) {
            Long busId = getBusId(bus);
            busKeywordRepository.save(new BusKeyword(userId, keyword, stationId, busId));
        }

    }

    Long getStationId(String station) {
        return stationRepository
                .findByName(station)
                .orElse(stationRepository.save(new Station(station)))
                .getId();
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
        busKeywordRepository.deleteByUserIdAndKeyword(userId, keyword);
    }
}
