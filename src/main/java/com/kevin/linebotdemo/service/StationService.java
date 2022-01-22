package com.kevin.linebotdemo.service;

import com.kevin.linebotdemo.model.Station;
import com.kevin.linebotdemo.repository.StationRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author liyanting
 */
@Service
@AllArgsConstructor
@Slf4j
public class StationService {

    private final StationRepository stationRepository;

    @Transactional(rollbackFor = Exception.class)
    public void createStation(Station station) {
        stationRepository.save(station);
    }

    public Station findByNameAndBearing(String stationName) {
        return stationRepository.findByName(stationName).get(0);
    }

    @Transactional(readOnly = true)
    public List<Long> getStationId(String stationName) {
        val station = stationRepository.findByName(stationName);
        List<Long> collect = station.stream().map(s -> s.getId()).collect(Collectors.toList());
        return collect;
    }

    @Transactional(rollbackFor = Exception.class)
    public void createAllStation(List<Station> stationList) {
        stationRepository.saveAll(stationList);
    }
}
