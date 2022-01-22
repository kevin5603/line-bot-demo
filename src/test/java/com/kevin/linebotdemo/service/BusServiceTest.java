package com.kevin.linebotdemo.service;

import com.kevin.linebotdemo.model.Bus;
import com.kevin.linebotdemo.repository.BusRepository;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class BusServiceTest {
    private BusService underTest;
    @MockBean
    private BusRepository busRepository;

    @BeforeEach
    void setUp() {
        underTest = new BusService(busRepository);
    }

    @Test
    void getBusIdIfBusExists() {
        // given
        String busName = "630";
        val bus = new Bus(1L, busName);
        when(busRepository.findByName(busName)).thenReturn(Optional.of(bus));

        // when
        Long busId = underTest.getBusId(busName);

        // then
        assertEquals(bus.getId(), busId);
        verify(busRepository, times(1)).findByName(busName);
        verify(busRepository, never()).save(any());

    }

    @Test
    void getBusIdIfBusNotExists() {
        // given
        String busName = "630";
        val bus = new Bus(1L, busName);
        when(busRepository.findByName(busName)).thenReturn(Optional.empty());
        when(busRepository.save(any())).thenReturn(bus);

        // when
        Long actualBusId = underTest.getBusId(busName);

        // then
        assertEquals(bus.getId(), actualBusId);
        verify(busRepository, times(1)).findByName(busName);
        verify(busRepository, times(1)).save(any());
    }
}