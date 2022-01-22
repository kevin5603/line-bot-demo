package com.kevin.linebotdemo.controller;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kevin.linebotdemo.config.BusAPIConst;
import com.kevin.linebotdemo.model.dto.BusDto;
import com.kevin.linebotdemo.model.dto.QueryDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.SignatureException;
import java.util.List;
import java.util.zip.GZIPInputStream;

import static com.kevin.linebotdemo.service.BusAPIService.Signature;
import static com.kevin.linebotdemo.service.BusAPIService.getServerTime;

/**
 * 之後刪除將功能全數移致BusAPIService
 * @author liyanting
 */
@RestController
@Slf4j
@Deprecated
public class BusEstimatedController {

    @Value("${bus.app-id}")
    private String APP_ID;
    @Value("${bus.app-key}")
    private String APP_KEY;

    private static final String URL = "https://ptx.transportdata.tw/MOTC/v2/Bus/EstimatedTimeOfArrival/City/%s/PassThrough/Station/%s?$select=%s&$filter=%s&$format=JSON";


    @Deprecated
    @PostMapping("/v2")
    public List<BusDto> getBusInfo(@RequestBody QueryDto dto) throws IOException {
        String path = buildPath(dto, URL);
        log.info(path);

        HttpURLConnection connection = getData(path);
        String respond = connection.getResponseCode() + BusAPIConst.SPACE + connection.getResponseMessage();
        log.info("回傳狀態:" + respond);

        String response = readData(connection);

        List<BusDto> list = jsonToObject(response);

        return list;
    }

    private String readData(HttpURLConnection connection) throws IOException {
        BufferedReader in;
        InputStreamReader reader = new InputStreamReader(new GZIPInputStream(connection.getInputStream()));
        in = new BufferedReader(reader);

        StringBuffer buffer = new StringBuffer();
        // 讀取回傳資料
        String line;
        while ((line = in.readLine()) != null) {
            buffer.append(line + "\n");
        }
        return buffer.toString();
    }

    private List<BusDto> jsonToObject(String response) {
        Gson gson = new Gson();
        Type type = new TypeToken<List<BusDto>>() {}.getType();
        List<BusDto> list = gson.fromJson(response, type);
        return list;
    }

    private HttpURLConnection getData(String path) throws IOException {
        String xdate = getServerTime();
        String SignDate = "x-date: " + xdate;

        String Signature = "";
        try {
            // 取得加密簽章
            Signature = Signature(SignDate, APP_KEY);
        } catch (SignatureException e1) {
            e1.printStackTrace();
        }

        String sAuth = "hmac username=\"" + APP_ID + "\", algorithm=\"hmac-sha1\", headers=\"x-date\", signature=\""
                + Signature + "\"";
        URL url = new URL(path);
        HttpURLConnection connection = null;

        connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization", sAuth);
        connection.setRequestProperty("x-date", xdate);
        connection.setRequestProperty("Accept-Encoding", "gzip");
        connection.setDoInput(true);
        connection.setDoOutput(true);
        return connection;
    }

    private String buildPath(QueryDto dto, String path) {
        return String.format(path, dto.getCity(), dto.getStationId(), dto.getSelect(), dto.getFilter());
    }

}
