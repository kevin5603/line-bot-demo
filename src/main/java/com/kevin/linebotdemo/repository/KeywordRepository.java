package com.kevin.linebotdemo.repository;

import com.kevin.linebotdemo.model.Keyword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author liyanting
 */
@Repository
public interface KeywordRepository extends JpaRepository<Keyword, Long> {

    /**
     * 用關鍵字找出其ID
     * @param word
     * @return
     */
    Optional<Keyword> findByWord(String word);
}
