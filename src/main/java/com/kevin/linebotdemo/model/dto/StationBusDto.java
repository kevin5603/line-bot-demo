package com.kevin.linebotdemo.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author liyanting
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StationBusDto {
    private String stationName;
    private String stationCode;
    private String busName;
}
