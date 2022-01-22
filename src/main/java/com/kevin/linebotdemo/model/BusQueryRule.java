package com.kevin.linebotdemo.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 公車系統查詢API參數規則
 * @author liyanting
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BusQueryRule {
    private String select;
    private String filter;
    private String top;
//    private String
//    private String
//    private String
//    private String
}
