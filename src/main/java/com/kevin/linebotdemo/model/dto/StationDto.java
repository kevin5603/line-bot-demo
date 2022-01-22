package com.kevin.linebotdemo.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kevin.linebotdemo.model.Station;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 場站
 * @author liyanting
 */
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class StationDto {
    @JsonProperty("StationID")
    private String stationID;

    @JsonProperty("StationName")
    private NameType stationName;

    @JsonProperty("StationAddress")
    private String stationAddress;

    @JsonProperty("Stops")
    private List<StopDto> stops;

    @JsonProperty("Bearing")
    private String bearing;

    public List<StationBusDto> toStationBusDto() {
        return stops.stream().map(stop -> {
            var busName = stop.getRouteName().getZh_tw();
            return new StationBusDto(stationName.getZh_tw(), stationID, busName);
        }).collect(Collectors.toList());
    }

    public Station toStation() {
        return new Station(stationID, stationName.getZh_tw(), stationAddress, bearing);
    }

}
