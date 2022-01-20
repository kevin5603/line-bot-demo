package com.kevin.linebotdemo.controller;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kevin.linebotdemo.model.dto.BusDto;
import com.kevin.linebotdemo.model.dto.QueryDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.SignatureException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.zip.GZIPInputStream;

@RestController
@Slf4j
public class BusEstimatedController {

    private String APP_ID = "280c33ff7e484178b7453137d9a1bd24";
    private static final String APP_KEY = "Q154KEsr409xweG9ZiueQo2gRPA";

    private static final String URL = "https://ptx.transportdata.tw/MOTC/v2/Bus/EstimatedTimeOfArrival/City/%s/PassThrough/Station/%s?$select=%s&$filter=%s&$format=JSON";

    @PostMapping("/v2")
    public List<BusDto> getBusInfo(@RequestBody QueryDto dto) throws IOException {
        String path = buildPath(dto, URL);
        log.info(path);

        HttpURLConnection connection = handleHeader(path);
        String respond = connection.getResponseCode() + " " + connection.getResponseMessage();
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
        String line, response = "";
        while ((line = in.readLine()) != null) {
            response += (line + "\n");
        }
        return response;
    }

    private List<BusDto> jsonToObject(String response) {
        Gson gson = new Gson();

        Type type = new TypeToken<List<BusDto>>() {
        }.getType();
        List<BusDto> list = gson.fromJson(response, type);
        return list;
    }

    private HttpURLConnection handleHeader(String path) throws IOException {
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
