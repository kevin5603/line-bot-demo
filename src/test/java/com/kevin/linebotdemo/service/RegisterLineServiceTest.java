package com.kevin.linebotdemo.service;

import com.kevin.linebotdemo.service.line.RegisterLineService;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
class RegisterLineServiceTest {

    @Autowired
    private RegisterLineService underTest;
    @MockBean
    private UserService userService;
    @MockBean
    private KeywordService keywordService;
    @MockBean
    private StationService stationService;
    @MockBean
    private BusService busService;
    @MockBean
    private BusKeywordService busKeywordService;


    @Test
    void registerCommandIfUserExists() {
        // given
        val userId = UUID.randomUUID().toString();
        var message = "回家 三民國中 630,617,645";
        when(userService.hasUser(userId)).thenReturn(true);
        val stationIdList = List.of(1L, 2L, 3L);
        when(stationService.getStationId("三民國中")).thenReturn(stationIdList);


        // when
        underTest.registerCommand(userId, message);

        // then
        verify(keywordService, times(1)).getKeywordId(userId, "回家");
        verify(stationService, times(1)).getStationId(any());
        verify(busService, times(3)).getBusId(any());
        verify(busKeywordService, times(9)).save(any(), any(), any());
    }
}