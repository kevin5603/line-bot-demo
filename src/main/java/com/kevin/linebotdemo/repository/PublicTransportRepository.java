package com.kevin.linebotdemo.repository;


import com.kevin.linebotdemo.model.dto.BusDto;

import java.util.List;

public interface PublicTransportRepository {

    List<BusDto> findByName(String name);
}

