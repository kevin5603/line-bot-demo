package com.kevin.linebotdemo.repository;

import com.kevin.linebotdemo.model.*;
import com.kevin.linebotdemo.model.dto.StationBusDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author liyanting
 */
@Repository
public interface BusKeywordRepository extends JpaRepository<BusKeyword, BusKeyword> {

    /**
     * 找出用戶關鍵字下的資料-供後續查詢公車資訊使用
     * @param userId
     * @param word
     * @return
     */
    @Query("Select new com.kevin.linebotdemo.model.dto.StationBusDto(s.name, s.code, b.name) from BusKeyword bk" +
            " left join Keyword k on k.id = bk.keywordId" +
            " left join Bus b on b.id = bk.busId" +
            " left join Station s on s.id = bk.stationId" +
            " where k.userId = :userId and k.word = :word")
    List<StationBusDto> findByUserIdAndKeyword(@Param("userId") String userId, @Param("word") String word);

    /**
     * 當用戶刪除關鍵字後，此中間表也要將該關鍵字pk相關資料刪除
     * @param keywordId 關鍵字pk
     */
    void deleteByKeywordId(Long keywordId);

}
