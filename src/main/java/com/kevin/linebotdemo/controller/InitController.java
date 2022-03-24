package com.kevin.linebotdemo.controller;


import com.kevin.linebotdemo.model.Bus;
import com.kevin.linebotdemo.model.StationGroup;
import com.kevin.linebotdemo.model.dto.StationDto;
import com.kevin.linebotdemo.repository.StationGroupRepository;
import com.kevin.linebotdemo.service.BusAPIService;
import com.kevin.linebotdemo.service.BusService;
import com.kevin.linebotdemo.service.StationService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.*;

/**
 * 暫時性方案
 * 直接Call API 初始化、更新資料庫
 * @author liyanting
 */
@RestController
@RequestMapping("api/v0/init")
@AllArgsConstructor
@Slf4j
public class InitController {

    private final BusAPIService busAPIService;
    private final StationService stationService;
    private final BusService busService;
    private final StationGroupRepository stationGroupRepository;

    /**
     * 初始化所有場站及公車資料
     */
    @PostMapping("station")
    public ResponseEntity initStation() {
        try {
            log.info("初始化Station資料");
            var stationDtoList = busAPIService.getData(busAPIService.getStationUrl())
                    .bodyToFlux(StationDto.class)
                    .collectList()
                    .block();
            log.info("prepare batch insert");

            saveAllBus(stationDtoList);
            saveAllStation(stationDtoList);
            saveAllStationGroup(stationDtoList);

            log.info(" station init finished...");
            return ResponseEntity.status(CREATED).build();
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).build();
        }
    }

    public void saveAllStationGroup(List<StationDto> stationDtoList) {
        List<StationGroup> stationGroupList = stationDtoList.stream()
                .filter(distinctByNameAndBearing())
                .map(stationDto -> new StationGroup(stationDto.getStationName().getZh_tw(), stationDto.getBearing()))
                .collect(Collectors.toList());
        List<StationGroup> stationGroups = stationGroupRepository.saveAll(stationGroupList);
        log.info("stationGroups size: " + stationGroups.size());
    }

    private Predicate<StationDto> distinctByNameAndBearing() {
        Set<String> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(t.getStationName().getZh_tw() + t.getBearing());
    }

    public void saveAllBus(List<StationDto> stationDtoList) {

        List<Bus> busList = stationDtoList.stream()
                .flatMap(stationDto -> stationDto.getStops().stream())
                .map(stopDto -> new Bus(stopDto.getRouteName().getZh_tw()))
                .distinct()
                .collect(Collectors.toList());
        List<Bus> buses = busService.saveAllBus(busList);
        log.info("buses.size():" + buses.size());
    }

    public void saveAllStation(List<StationDto> stationDtoList) {
        var stationList = stationDtoList.stream()
                .map(stationDto -> stationDto.toStation())
                .collect(Collectors.toList());
        stationService.createAllStation(stationList);
        log.info("stationList.size(): " + stationList.size());
    }

}
