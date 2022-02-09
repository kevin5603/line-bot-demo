package com.kevin.linebotdemo.repository;

import com.kevin.linebotdemo.model.*;
import com.kevin.linebotdemo.model.dto.BusKeywordInfoDto;
import com.kevin.linebotdemo.model.dto.StationBusDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * @author liyanting
 */
@Repository
public interface BusKeywordRepository extends JpaRepository<BusKeyword, BusKeyword> {

    /**
     * 找出用戶關鍵字下的資料-供後續查詢公車資訊使用
     * @param userId 使用者ID
     * @param word 關鍵字
     * @return
     */
    @Query("SELECT new com.kevin.linebotdemo.model.dto.StationBusDto(s.name, s.code, b.name) FROM BusKeyword bk" +
            " LEFT JOIN Keyword k ON k.id = bk.keywordId" +
            " LEFT JOIN Bus b ON b.id = bk.busId" +
            " LEFT JOIN Station s ON s.id = bk.stationId" +
            " where k.userId = :userId and k.word = :word")
    List<StationBusDto> findByUserIdAndKeyword(@Param("userId") String userId, @Param("word") String word);

    /**
     * 當用戶刪除關鍵字後，此中間表也要將該關鍵字pk相關資料刪除
     * @param keywordId 關鍵字ID
     */
    void deleteByKeywordId(Long keywordId);

    /**
     * 查詢使用者以註冊的關鍵字相關場站車輛資訊
     * @param lineUserId 使用者ID
     * @return
     */
    @Query("SELECT distinct new com.kevin.linebotdemo.model.dto.BusKeywordInfoDto(k.word, s.name, b.name) FROM BusKeyword bk " +
            "LEFT JOIN Bus b ON bk.busId = b.id " +
            "LEFT JOIN Keyword k ON bk.keywordId = k.id " +
            "LEFT JOIN Station s ON bk.stationId = s.id " +
            "WHERE k.userId = :lineUserId")
    List<BusKeywordInfoDto> findAllKeywordInfoByUserId(@Param("lineUserId") String lineUserId);

    /**
     * 刪除使用者該關鍵字下得所有到站資訊
     * @param lineUserId 使用者ID
     * @param word 關鍵字
     */
    @Modifying
    @Query("DELETE FROM BusKeyword bk " +
            "WHERE bk.keywordId IN (SELECT id FROM Keyword WHERE userId = :lineUserId AND word = :word)")
    void deleteByUserIdAndKeyWord(@Param("lineUserId") String lineUserId, @Param("word") String word);

    /**
     * 刪除使用者該關鍵字指定的公車到站資訊
     * @param lineUserId 使用者ID
     * @param word 關鍵字
     * @param busList 公車名稱列表
     */
    @Modifying
    @Query("DELETE FROM BusKeyword bk " +
            "WHERE bk.keywordId IN (SELECT id FROM Keyword WHERE userId = :lineUserId AND word = :word)" +
            "AND bk.busId IN (SELECT id FROM Bus WHERE name in :busList)")
    void deleteByUserIdAndKeyWordAndBusId(@Param("lineUserId") String lineUserId, @Param("word") String word,
                                          @Param("busList") List<String> busList);




}
