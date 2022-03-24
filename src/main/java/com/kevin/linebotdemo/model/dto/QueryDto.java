package com.kevin.linebotdemo.model.dto;


import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * 查詢公車API所需參數
 * @author liyanting
 */
@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class QueryDto {

    private String city = "Taipei";
    @NonNull
    private String stationId;
    private String select = "StopName,RouteName";
    @NonNull
    private String filter;
}
