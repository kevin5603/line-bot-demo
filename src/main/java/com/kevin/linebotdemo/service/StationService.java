package com.kevin.linebotdemo.service;

import com.kevin.linebotdemo.exception.UnexpectCommandException;
import com.kevin.linebotdemo.model.Station;
import com.kevin.linebotdemo.model.StationGroup;
import com.kevin.linebotdemo.repository.StationGroupRepository;
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
    private final StationGroupRepository stationGroupRepository;

    @Transactional(rollbackFor = Exception.class)
    public void createStation(Station station) {
        stationRepository.save(station);
    }

    public List<Station> findByNameAndBearing(String stationName, String bearing) {
        return stationRepository.findByNameAndBearing(stationName, bearing);
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

    /**
     * 利用場站群組ID查詢相同方向的場站
     * @param stationGroupId
     * @return
     */
    public List<Long> findStationByStationGroupId(String stationGroupId) {
        StationGroup stationGroup = stationGroupRepository.findById(Long.parseLong(stationGroupId))
                .orElseThrow(() -> new UnexpectCommandException("查無場站，請確認場站群組ID是否輸入正確"));
        return stationRepository.findIdByNameAndBearing(stationGroup.getStationName(), stationGroup.getBearing());

    }
}
