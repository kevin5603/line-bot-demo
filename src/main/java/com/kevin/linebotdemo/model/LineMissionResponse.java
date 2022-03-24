package com.kevin.linebotdemo.model;

import com.kevin.linebotdemo.config.LineResponseCode;
import lombok.*;

/**
 * @author liyanting
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor
public class LineMissionResponse<T> {

    /** 處理結果代碼 */
    @NonNull
    private LineResponseCode code;
    private T body;

}
