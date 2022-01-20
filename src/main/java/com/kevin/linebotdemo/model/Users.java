package com.kevin.linebotdemo.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.Instant;

/**
 * line使用者資訊
 * @author liyanting
 */
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Users {

    @Id
    private String userId;
    @JsonFormat(pattern = "yyyy-MM-dd HH-mm-ss")
    private Instant updateDate;
    @JsonFormat(pattern = "yyyy-MM-dd HH-mm-ss")
    private Instant createDate;
}
