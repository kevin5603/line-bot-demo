package com.kevin.linebotdemo.service.line;

import com.kevin.linebotdemo.model.LineMissionResponse;
import com.kevin.linebotdemo.model.Station;
import com.kevin.linebotdemo.model.StationGroup;
import com.kevin.linebotdemo.repository.StationGroupRepository;
import com.kevin.linebotdemo.service.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.kevin.linebotdemo.config.BusAPIConst.COMMA;
import static com.kevin.linebotdemo.config.BusAPIConst.SPACE;
import static com.kevin.linebotdemo.config.LineResponseCode.CRATE;
import static com.kevin.linebotdemo.config.LineResponseCode.SUCCESS;

/**
 * @author liyanting
 */
@Service
@AllArgsConstructor
@Slf4j
public class RegisterLineService {

    private final UserService userService;
    private final KeywordService keywordService;
    private final StationService stationService;
    private final BusService busService;
    private final BusKeywordService busKeywordService;
    private final StationGroupRepository stationGroupRepository;


    @Transactional(rollbackFor = Exception.class)
    public LineMissionResponse registerCommand(String lineUserId, String message) {
        if (!userService.hasUser(lineUserId)) {
            userService.createUser(lineUserId);
        }

        val dto = RegisterKeywordDTO.getDto(message);

        Long keywordId = keywordService.getKeywordId(lineUserId, dto.getKeyword());


        List<Long> stationGroupIdList  = checkStationDirection(dto.getStationName());

        for (String busName: dto.busList) {
            val busId = busService.getBusId(busName);
            for (Long stationGroupId : stationGroupIdList) {
                busKeywordService.save(keywordId, stationGroupId, busId);
            }
        }
        return new LineMissionResponse(CRATE);
    }

    /**
     * 先檢查場站是否有方向性，若有則需再去詢問使用者是要註冊哪個方向
     * @param stationName
     * @return
     */
    private List<Long> checkStationDirection(String stationName) {
        List<StationGroup> stationGroups = stationGroupRepository.findByStationName(stationName);
        if (stationGroups.size() != 1) {
            ask();
        } else {
            StationGroup stationGroup = stationGroups.get(0);
            List<Long> stationIdList = stationService.findByNameAndBearing(stationGroup.getStationName(), stationGroup.getBearing())
                    .stream().map(Station::getId).collect(Collectors.toList());
            return stationIdList;
        }
        return stationService.getStationId(stationName);
    }

    /**
     * 場站有多個方向，需要再詢問使用者要使用哪個方向
     */
    private void ask() {
    }

    public LineMissionResponse registerCommand(String lineUserId, String message, String stationGroupId) {
        val dto = RegisterKeywordDTO.getDto(message);

        Long keywordId = keywordService.getKeywordId(lineUserId, dto.getKeyword());

        List<Long> stationIdList  = stationService.findStationByStationGroupId(stationGroupId);

        for (String busName: dto.busList) {
            val busId = busService.getBusId(busName);
            for (Long stationId : stationIdList) {
                busKeywordService.save(keywordId, stationId, busId);
            }
        }
        val responseMessage = "新增關鍵字完畢";
        log.info(responseMessage);
        return new LineMissionResponse(CRATE, responseMessage);
    }

    @Data
    @AllArgsConstructor
    public static class RegisterKeywordDTO {
        private String keyword;
        private String stationName;
        private List<String> busList;

        public static RegisterKeywordDTO getDto(String message) {
            val msg = message.split(SPACE);
            String keyword = msg[0];
            String station = msg[1];
            var busList = Arrays.asList(msg[2].split(COMMA));
            return new RegisterKeywordDTO(keyword, station, busList);
        }
    }
}
