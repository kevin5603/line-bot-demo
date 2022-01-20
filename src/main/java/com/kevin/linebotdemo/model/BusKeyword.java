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
    private Long id;
    @Id
    private String userId;
    private Long keywordId;
    private Long busId;

    public BusKeyword(String userId, Long keywordId, Long busId) {
        this.userId = userId;
        this.keywordId = keywordId;
        this.busId = busId;
    }
}
