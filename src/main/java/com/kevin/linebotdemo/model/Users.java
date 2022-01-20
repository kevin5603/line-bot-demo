package com.kevin.linebotdemo.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * @author liyanting
 */
@Entity
@Data
public class Users {

    @Id
    private String userId;
}
