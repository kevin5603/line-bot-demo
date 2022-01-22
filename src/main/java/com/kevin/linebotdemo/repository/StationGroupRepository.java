package com.kevin.linebotdemo.repository;

import com.kevin.linebotdemo.model.StationGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * @author liyanting
 */
@Repository
public interface StationGroupRepository extends JpaRepository<StationGroup, Long> {

    Optional<StationGroup> findByStationNameAndBearing(String stationName, String bearing);

    Long countByStationName(String stationName);

    List<StationGroup> findByStationName(String stationName);
}
