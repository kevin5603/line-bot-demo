package com.kevin.linebotdemo.repository;

import com.kevin.linebotdemo.model.Station;
import com.kevin.linebotdemo.model.dto.StationBusDto;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * @author liyanting
 */
@Repository
public interface StationRepository extends JpaRepository<Station, Long> {

    List<Station> findByName(String stationName);

    List<Station> findByNameAndBearing(String stationName, String bearing);

    @Query("Select s.id from Station s" +
            " where s.name = :stationName and s.bearing = :bearing")
    List<Long> findIdByNameAndBearing(@Param("stationName") String stationName, @Param("bearing") String bearing);

}
