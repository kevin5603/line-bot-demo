package com.kevin.linebotdemo.service;

import com.kevin.linebotdemo.model.Station;
import com.kevin.linebotdemo.repository.StationRepository;
import lombok.AllArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author liyanting
 */
@Service
@AllArgsConstructor
public class StationService {

    private final StationRepository stationRepository;

    @Transactional(rollbackFor = Exception.class)
    public void createStation(Station station) {
        stationRepository.save(station);
    }

    public Station findByName(String stationName) {
        return stationRepository.findByName(stationName).get();
    }

    @Transactional(readOnly = true)
    public Long getStationId(String stationName) {
        val station = stationRepository.findByName(stationName);
        return station.get().getId();
    }
}
