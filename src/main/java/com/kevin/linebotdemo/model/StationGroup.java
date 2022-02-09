package com.kevin.linebotdemo.model;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import static javax.persistence.GenerationType.IDENTITY;

/**
 * 因為公車站牌同方向可能多個，但是我們在使用時只在意是否為同一個站點，因此使用群組將其合併
 * @author liyanting
 */
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class StationGroup {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    private String stationName;

    private String bearing;

    public StationGroup(String stationName, String bearing) {
        this.stationName = stationName;
        this.bearing = bearing;
    }
}
