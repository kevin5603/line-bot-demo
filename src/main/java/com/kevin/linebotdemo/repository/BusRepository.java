package com.kevin.linebotdemo.repository;

import com.kevin.linebotdemo.model.Bus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author liyanting
 */
@Repository
public interface BusRepository extends JpaRepository<Bus, Long> {

    /**
     *
     * @param name
     * @return
     */
    Optional<Bus> findByName(String name);
}
