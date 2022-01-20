package com.kevin.linebotdemo.model.dto;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

/**
 * @author liyanting
 */
@Data
class NameType {

    @SerializedName("Zh_tw")
    private String zh_tw;
    @SerializedName("En")
    private String en;
}
