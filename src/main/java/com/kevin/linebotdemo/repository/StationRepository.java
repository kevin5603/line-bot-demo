package com.kevin.linebotdemo.repository;

import com.kevin.linebotdemo.model.Station;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * @author liyanting
 */
@Repository
public interface StationRepository extends JpaRepository<Station, Long> {

    List<Station> findByName(String stationName);

}
