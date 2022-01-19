package com.kevin.linebotdemo.controller;

import com.kevin.linebotdemo.model.Bus;
import com.kevin.linebotdemo.model.QueryDto;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class BusEstimatedControllerTest {

    @Test
    void main() throws IOException {
        BusEstimatedController c = new BusEstimatedController();
        QueryDto path1 = new QueryDto("8663", "RouteName/Zh_tw eq '藍36'");
        QueryDto path2 = new QueryDto("966", "RouteName/Zh_tw eq '903' or RouteName/Zh_tw eq '645副' or RouteName/Zh_tw eq '645' or RouteName/Zh_tw eq '民權幹線'");

        List<Bus> list1 = c.getBusInfo(path1);
        List<Bus> list2 = c.getBusInfo(path2);

        Stream.concat(list1.stream(), list2.stream()).forEach(bus -> {
            log.info(String.format("車名:%s 預計抵達%s 時間%d分%d秒",
                    bus.getRouteName().getZh_tw(),
                    bus.getStopName().getZh_tw(),
                    bus.getEstimateTime() / 60, bus.getEstimateTime() % 60));
        });
    }
}