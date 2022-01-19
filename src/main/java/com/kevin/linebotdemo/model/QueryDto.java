package com.kevin.linebotdemo.model;


import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

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
