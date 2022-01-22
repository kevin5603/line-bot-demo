package com.kevin.linebotdemo.model;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * @author liyanting
 */
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor
public class Keyword {
    @Id
    private Long id;

    @NonNull
    private String userId;

    @NonNull
    private String word;
}
