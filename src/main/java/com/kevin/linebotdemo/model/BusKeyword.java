package com.kevin.linebotdemo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import java.io.Serializable;

/**
 * @author liyanting
 */
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@IdClass(BusKeyword.class)
public class BusKeyword implements Serializable {

    @Id
    private Long keywordId;
    @Id
    private Long stationGroupId;
    @Id
    private Long busId;

}
