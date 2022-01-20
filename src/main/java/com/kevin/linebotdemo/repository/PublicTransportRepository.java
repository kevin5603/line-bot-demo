package com.kevin.linebotdemo.repository;


import com.kevin.linebotdemo.model.dto.BusDto;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author liyanting
 */
@Repository
public interface PublicTransportRepository {

    List<BusDto> findByName(String name);
}

