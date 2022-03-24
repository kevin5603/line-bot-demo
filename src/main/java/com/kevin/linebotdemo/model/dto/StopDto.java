package com.kevin.linebotdemo.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 站牌DTO
 * @author liyanting
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StopDto {

    @JsonProperty("StopID")
    private String stopId;

    @JsonProperty("StopName")
    private NameType stopName;

    @JsonProperty("RouteID")
    private String routeID;

    @JsonProperty("RouteName")
    private NameType routeName;

}
