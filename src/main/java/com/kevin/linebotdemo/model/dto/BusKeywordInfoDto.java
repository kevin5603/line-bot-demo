package com.kevin.linebotdemo.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BusKeywordInfoDto {
    private String keyword;
    private String stationName;
    private String busName;
}
