package com.kevin.linebotdemo.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;

import java.util.Date;

/**
 * 公車DTO
 *
 * @author liyanting
 */
@Data
@ToString
public class BusDto {

    @JsonProperty("StopName")
    private NameType stopName;

    @JsonProperty("Direction")
    private int direction;

    @JsonProperty("RouteName")
    private NameType routeName;

    @JsonProperty("EstimateTime")
    private int estimateTime;

    @JsonProperty("StopStatus")
    private int stopStatus;

    @JsonProperty("SrcUpdateTime")
    private Date srcUpdateTime;

    @JsonProperty("UpdateTime")
    private Date updateTime;

}