package com.kevin.linebotdemo.repository;

import com.kevin.linebotdemo.model.Keyword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * @author liyanting
 */
@Repository
public interface KeywordRepository extends JpaRepository<Keyword, Long> {


    /**
     * 用line user id找出該用戶已註冊的關鍵字
     * @param userId 使用者ID
     * @return List<Keyword>
     */
    List<Keyword> findByUserId(String userId);

    /**
     * 刪除該用戶指定關鍵字
     * @param userId 使用者ID
     * @param word 關鍵字
     */
    void deleteByUserIdAndWord(String userId, String word);

    /**
     * 要找出用戶關鍵字的primary key 供後續刪除bus_keyword表使用
     * @param userId
     * @param keyword
     * @return
     */
    Optional<Keyword> findByUserIdAndWord(String userId, String keyword);
}
