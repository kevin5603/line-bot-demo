package com.kevin.linebotdemo.service;

import com.kevin.linebotdemo.config.LineResponseCode;
import com.kevin.linebotdemo.model.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.val;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

import static com.kevin.linebotdemo.config.LineResponseCode.SUCCESS;

/**
 * @author liyanting
 */
@Service
@AllArgsConstructor
public class RegisterLineService {

    private final UserService userService;
    private final KeywordService keywordService;
    private final StationService stationService;
    private final BusService busService;
    private final BusKeywordService busKeywordService;


    @Transactional(rollbackFor = Exception.class)
    public LineMissionResponse registerCommand(String userId, String message) {
        val user = userService.getUser(userId);
        val dto = RegisterKeywordDTO.getDto(message);

        Long keywordId = keywordService.getKeywordId(userId, dto.getKeyword());

        Long stationId  = stationService.getStationId(dto.getStationName());

        for (String busName: dto.busList) {
            Bus bus = busService.findByBusId(busName);
            val busId = bus.getId();

            busKeywordService.save(keywordId, stationId, busId);
        }
        val lineMissionResponse = new LineMissionResponse(SUCCESS);
        return lineMissionResponse;

    }

    @Data
    @AllArgsConstructor
    private static class RegisterKeywordDTO {
        private String keyword;
        private String stationName;
        private List<String> busList;

        private static RegisterKeywordDTO getDto(String message) {
            val msg = message.split(" ");
            String keyword = msg[0];
            String station = msg[1];
            var busList = Arrays.asList(msg[2].split(","));
            return new RegisterKeywordDTO(keyword, station, busList);
        }
    }
}
