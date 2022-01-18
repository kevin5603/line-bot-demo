package com.kevin.linebotdemo.controller;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kevin.linebotdemo.model.Bus;
import com.kevin.linebotdemo.model.QueryDto;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.web.bind.annotation.*;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.SignatureException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.GZIPInputStream;

@RestController
@Slf4j
public class BusEstimatedController {

    private static final String APP_ID = "280c33ff7e484178b7453137d9a1bd24";
    private static final String APP_KEY = "Q154KEsr409xweG9ZiueQo2gRPA";

    private static final String URL = "https://ptx.transportdata.tw/MOTC/v2/Bus/EstimatedTimeOfArrival/City/%s/%s";

//    public static void main(String[] args) throws IOException {
//        BusEstimatedController c = new BusEstimatedController();
//        val queryBL36Dto = new QueryDto("Taipei", "藍36", "StopName,RouteName", "StopName/Zh_tw eq '上灣仔' and Direction eq '1'");
//        val query645Dto = new QueryDto("Taipei", "645",
//                "StopName,RouteName",
//                "StopName/Zh_tw eq '內湖行政大樓' and Direction eq '1'");
//
//        val query903Dto = new QueryDto("Taipei", "903",
//                "StopName,RouteName",
//                "StopName/Zh_tw eq '內湖行政大樓' and Direction eq '1'");
//
//        c.getBusInfo(queryBL36Dto);
//
//        while (true) {
//            List<Bus> collect = Stream.concat(c.getBusInfo(query903Dto).stream(),
//                                            Stream.concat(c.getBusInfo(queryBL36Dto).stream(),
//                                                    c.getBusInfo(query645Dto).stream()))
//                    .collect(Collectors.toList());
//            for (Bus bus: collect) {
//                log.info(String.format("車名:%s 預計抵達%s 時間%d分%d秒",
//                        bus.getRouteName().getZh_tw(),
//                        bus.getStopName().getZh_tw(),
//                        bus.getEstimateTime() / 60, bus.getEstimateTime() % 60));
//            }
//
//
//            try {
//                Thread.sleep(20000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//
////        String s = c.buildPath(queryDto, URL);
////        System.out.println(s);
//    }

    @PostMapping("/v2")
    public List<Bus> getBusInfo(@RequestBody QueryDto dto) throws IOException {

        String path = buildPath(dto, URL);
        log.info(path);


        String xdate = getServerTime();
        String SignDate = "x-date: " + xdate;
        String respond = "";

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

        respond = connection.getResponseCode() + " " + connection.getResponseMessage();
        System.out.println("回傳狀態:" + respond);
        BufferedReader in;

        InputStreamReader reader = new InputStreamReader(new GZIPInputStream(connection.getInputStream()));
        in = new BufferedReader(reader);

        StringBuffer buffer = new StringBuffer();
        // 讀取回傳資料
        String line, response = "";
        while ((line = in.readLine()) != null) {
            response += (line + "\n");
        }

        Gson gson = new Gson();


        Type type = new TypeToken<List<Bus>>() {
        }.getType();
        List<Bus> list = gson.fromJson(response, type);

        list.forEach(bus -> log.info(bus.toString()));
        log.info("size: " + list.size());
        log.info("end");
        return list;


    }

    private String buildPath(QueryDto dto, String path) {
        path = String.format(path, dto.getCity(), dto.getRouteName());
        if (!dto.getSelect().isEmpty()) {
            path += "?$select=" + dto.getSelect();
        }

        if (!dto.getFilter().isEmpty()) {
            path += "&$filter=" + dto.getFilter();
        }

        if (dto.getSelect().isEmpty() && dto.getFilter().isEmpty()) {
            path += "$format=JSON";
        } else {
            path += "&$format=JSON";
        }
        return path;
    }

    // 取得當下UTC時間
    public static String getServerTime() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        return dateFormat.format(calendar.getTime());
    }

    public static String Signature(String xData, String AppKey) throws java.security.SignatureException {
        try {
            final Base64.Encoder encoder = Base64.getEncoder();
            // get an hmac_sha1 key from the raw key bytes
            SecretKeySpec signingKey = new SecretKeySpec(AppKey.getBytes("UTF-8"), "HmacSHA1");

            // get an hmac_sha1 Mac instance and initialize with the signing key
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(signingKey);

            // compute the hmac on input data bytes
            byte[] rawHmac = mac.doFinal(xData.getBytes("UTF-8"));
            String result = encoder.encodeToString(rawHmac);
            return result;

        } catch (Exception e) {
            throw new SignatureException("Failed to generate HMAC : " + e.getMessage());
        }
    }
}
