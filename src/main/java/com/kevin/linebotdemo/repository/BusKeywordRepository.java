package com.kevin.linebotdemo.repository;

import com.kevin.linebotdemo.model.BusKeyword;
import com.kevin.linebotdemo.model.BusKeywordCompositeId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author liyanting
 */
@Repository
public interface BusKeywordRepository extends JpaRepository<BusKeyword, BusKeywordCompositeId> {

    /**
     * 用line user id找出該用戶已註冊的關鍵字
     * @param userId
     * @return
     */
    List<BusKeyword> findBusKeywordsByUserId(String userId);

    /**
     * 刪除該用戶指定關鍵字
     * @param userId
     * @param keyId
     */
    void deleteByUserIdAndKeyword(String userId, String keyword);
}
