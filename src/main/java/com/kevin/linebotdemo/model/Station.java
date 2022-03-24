package com.kevin.linebotdemo.model;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import static javax.persistence.GenerationType.IDENTITY;

/**
 * 公車站點
 * @author liyanting
 */
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor
public class Station {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;
    /** 場站代碼 */
    @NonNull
    private String code;
    /** 場站中文代碼 */
    @NonNull
    private String name;
    /** 場站地址(有方向性) */
    @NonNull
    private String address;

    /** 方向 (資料來源該項可能為null使用該欄位要特別注意)*/
    private String bearing;

    public Station(String name) {
        this.name = name;
    }

    public Station(@NonNull String code, @NonNull String name, @NonNull String address, String bearing) {
        this.code = code;
        this.name = name;
        this.address = address;
        this.bearing = bearing;
    }
}
