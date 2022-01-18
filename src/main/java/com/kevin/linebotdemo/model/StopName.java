package com.kevin.linebotdemo.model;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class StopName {

    @SerializedName("Zh_tw")
    private String zh_tw;
    @SerializedName("En")
    private String en;
}
