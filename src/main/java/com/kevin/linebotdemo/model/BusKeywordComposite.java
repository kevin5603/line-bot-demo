package com.kevin.linebotdemo.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;

/**
 * @author liyanting
 */
//@Entity
//@Data
@Deprecated
public class BusKeywordComposite implements Serializable {
    @Id
    private Long keywordId;
    @Id
    private Long stationId;
    @Id
    private Long busId;
}
