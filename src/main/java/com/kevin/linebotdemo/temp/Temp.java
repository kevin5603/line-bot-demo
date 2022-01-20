package com.kevin.linebotdemo.temp;

import com.kevin.linebotdemo.model.BusKeyword;
import com.kevin.linebotdemo.repository.BusKeywordRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class Temp {
    private final BusKeywordRepository repository;

    public Temp(BusKeywordRepository repository) {
        this.repository = repository;
        a();
    }


    public void a() {

        for (int i = 1; i <= 5; i++) {
            BusKeyword busKeyword = new BusKeyword("person" + i, 1L, 1L);
            repository.save(busKeyword);
        }
        repository.flush();

        repository.findAll().forEach(i -> log.info(i.toString()));

    }
}
