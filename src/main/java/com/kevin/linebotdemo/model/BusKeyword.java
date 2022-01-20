package com.kevin.linebotdemo.model;

import lombok.*;

import javax.persistence.*;

/**
 * @author liyanting
 */
@Entity
@IdClass(BusKeywordCompositeId.class)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BusKeyword {

    @Id
    private String userId;
    @Id
    private String keyword;
    private Long stationId;
    private Long busId;

}
