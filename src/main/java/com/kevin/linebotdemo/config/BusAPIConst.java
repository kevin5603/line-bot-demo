package com.kevin.linebotdemo.config;

/**
 * 公共運輸旅運資料服務API
 * @author liyanting
 */
public class BusAPIConst {

    /** 取得台北所有場站資訊  */
    public static final String GET_ALL_TAIPEI_STATION = "https://ptx.transportdata.tw/MOTC/v2/Bus/Station/City/Taipei";
    /** 取得指定場站公車預定到站時間 */
    public static final String GET_BUS_ESTIMATE_TIME_BY_STATION = "https://ptx.transportdata.tw/MOTC/v2/Bus/EstimatedTimeOfArrival/City/Taipei/PassThrough/Station/%s";
    /** 指令皆以空白做區分 */
    public static final String SPACE = " ";
    /** 逗號 */
    public static final String COMMA = ",";
}
