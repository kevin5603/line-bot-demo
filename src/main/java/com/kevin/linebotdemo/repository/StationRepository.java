package com.kevin.linebotdemo.repository;

import com.kevin.linebotdemo.model.Station;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author liyanting
 */
@Repository
public interface StationRepository extends JpaRepository<Station, Long> {

    Optional<Station> findByName(String stationName);
}
