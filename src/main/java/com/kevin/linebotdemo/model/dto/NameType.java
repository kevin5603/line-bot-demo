package com.kevin.linebotdemo.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author liyanting
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NameType {

    @JsonProperty("Zh_tw")
    private String zh_tw;

    @JsonProperty("En")
    private String en;
}
