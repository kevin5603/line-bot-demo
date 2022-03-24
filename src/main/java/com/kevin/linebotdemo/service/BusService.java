package com.kevin.linebotdemo.service;

import com.kevin.linebotdemo.model.Bus;
import com.kevin.linebotdemo.repository.BusRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author liyanting
 */
@Service
@AllArgsConstructor
@Slf4j
public class BusService {
    private final BusRepository busRepository;

    @Transactional(rollbackFor = Exception.class)
    public Bus saveBus(String busName) {
        return busRepository.save(new Bus(busName));
    }

    public List<Bus> saveAllBus(List<Bus> busList) {
        return busRepository.saveAll(busList);
    }

    /**
     * 取得公車pk，若資料庫沒該公車資訊則新增資料進DB
     * @param busName
     * @return
     */
    public Long getBusId(String busName) {
        val optionalBus = busRepository.findByName(busName);
        Bus bus = optionalBus.isPresent() ? optionalBus.get() : saveBus(busName);
        return bus.getId();
    }
}
