package com.kevin.linebotdemo.model;

import com.kevin.linebotdemo.config.LineResponseCode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author liyanting
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LineMissionResponse {

    /** 處理結果代碼 */
    private LineResponseCode code;

}
