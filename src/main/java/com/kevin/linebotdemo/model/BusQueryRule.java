package com.kevin.linebotdemo.model;


import lombok.*;

/**
 * 公車系統查詢API參數規則
 * @author liyanting
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor
public class BusQueryRule {
    @NonNull
    private String select;
    @NonNull
    private String filter;
    @NonNull
    private String top;
    /** 場站查詢才會用到 */
    private String stationCode;
}
