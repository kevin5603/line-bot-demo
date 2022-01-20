package com.kevin.linebotdemo.model.dto;

import com.google.gson.annotations.SerializedName;
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

    @SerializedName("StopName")
    private NameType nameType;

    @SerializedName("Direction")
    private int direction;

    @SerializedName("RouteName")
    private NameType routeName;

    @SerializedName("EstimateTime")
    private int estimateTime;

    @SerializedName("StopStatus")
    private int stopStatus;

    @SerializedName("SrcUpdateTime")
    private Date srcUpdateTime;

    @SerializedName("UpdateTime")
    private Date updateTime;

}