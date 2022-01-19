package com.kevin.linebotdemo.model;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.ToString;

import java.util.Date;

@Data
@ToString
public class Bus {

    @SerializedName("StopName")
    private StopName stopName;

    @SerializedName("Direction")
    private int direction;

    @SerializedName("RouteName")
    private StopName routeName;

    @SerializedName("EstimateTime")
    private int estimateTime;

    @SerializedName("StopStatus")
    private int stopStatus;

    @SerializedName("SrcUpdateTime")
    private Date srcUpdateTime;

    @SerializedName("UpdateTime")
    private Date updateTime;
}
