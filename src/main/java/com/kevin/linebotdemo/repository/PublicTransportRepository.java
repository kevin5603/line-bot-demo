package com.kevin.linebotdemo.repository;


import com.kevin.linebotdemo.model.Bus;

import java.util.List;

public interface PublicTransportRepository {

    List<Bus> findByName(String name);
}

