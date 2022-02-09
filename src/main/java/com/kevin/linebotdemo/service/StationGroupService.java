package com.kevin.linebotdemo.service;

import com.kevin.linebotdemo.model.StationGroup;
import com.kevin.linebotdemo.repository.StationGroupRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class StationGroupService {

    private final StationGroupRepository stationGroupRepository;


    public List<StationGroup> getStations(String stationName) {
        return stationGroupRepository.findByStationName(stationName);
    }
}
