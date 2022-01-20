package com.kevin.linebotdemo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import java.io.Serializable;

import static javax.persistence.GenerationType.AUTO;

/**
 * @author liyanting
 */
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BusKeywordCompositeId implements Serializable {

    @Id
    @GeneratedValue(strategy = AUTO)
    private Long id;
    @Id
    private String userId;
}
