package com.kevin.linebotdemo.service;

import com.kevin.linebotdemo.model.BusQueryRule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.SignatureException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Consumer;

import static com.kevin.linebotdemo.config.BusAPIConst.GET_ALL_TAIPEI_STATION;
import static com.kevin.linebotdemo.config.BusAPIConst.GET_BUS_ESTIMATE_TIME_BY_STATION;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * @author liyanting
 */
@Service
@Slf4j
public class BusAPIService {

    private static final String X_DATE = "x-date";
    private static final String GZIP = "gzip";
    private WebClient webClient = WebClient.create();

    @Value("${bus.app-id}")
    private String appId;

    @Value("${bus.app-key}")
    private String appKey;

    public String getStationUrl() {
        return builder(GET_ALL_TAIPEI_STATION)
                .select("StationID,StationName,StationAddress,Stops,Bearing")
                .build();
    }

    public String getBusEstimateTime(BusQueryRule busQueryRule) {
        // "StopName,RouteName"
        return builder(GET_BUS_ESTIMATE_TIME_BY_STATION)
                .select(busQueryRule.getSelect())
                .filter(busQueryRule.getFilter())
                .build();

    }

    /** 取得當下UTC時間 */
    public static String getServerTime() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        return dateFormat.format(calendar.getTime());
    }

    public WebClient.ResponseSpec getData(String path) {
        return webClient.get()
                .uri(path)
                .headers(handleHeader())
                .retrieve();
    }

    private Consumer<HttpHeaders> handleHeader() {
        String xDate = getServerTime();
        return headers -> {
            headers.add(HttpHeaders.AUTHORIZATION, getAuth(xDate));
            headers.add(HttpHeaders.ACCEPT_ENCODING, GZIP);

            headers.add(X_DATE, getServerTime());
        };
    }

    private String getAuth(String xDate) {
        String SignDate = "x-date: " + xDate;
        String Signature = "";
        try {
            // 取得加密簽章
            Signature = Signature(SignDate, appKey);
        } catch (SignatureException e1) {
            e1.printStackTrace();
        }

        String sAuth = "hmac username=\"" + appId + "\", algorithm=\"hmac-sha1\", headers=\"x-date\", signature=\""
                + Signature + "\"";
        return sAuth;
    }

    public static String Signature(String xData, String appKey) throws java.security.SignatureException {
        try {
            final Base64.Encoder encoder = Base64.getEncoder();
            // get an hmac_sha1 key from the raw key bytes
            SecretKeySpec signingKey = new SecretKeySpec(appKey.getBytes(UTF_8), "HmacSHA1");

            // get an hmac_sha1 Mac instance and initialize with the signing key
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(signingKey);

            // compute the hmac on input data bytes
            byte[] rawHmac = mac.doFinal(xData.getBytes(UTF_8));
            String result = encoder.encodeToString(rawHmac);
            return result;

        } catch (Exception e) {
            throw new SignatureException("Failed to generate HMAC : " + e.getMessage());
        }
    }

    private static BusAPIBuilder builder(String url) {
        return new BusAPIBuilder(url);
    }

    public static class BusAPIBuilder {

        private String url;
        private String selectParam;
        private String filterParam;
        private static final String PREFIX = "?%";

        private BusAPIBuilder(String url) {
            this.url = url;
        }

        public BusAPIBuilder select(String selectParam) {
            this.selectParam = selectParam;
            if (this.selectParam != null && !this.selectParam.isEmpty()) {
                url += "?$select=" + this.selectParam;
            }
            return this;
        }

        public BusAPIBuilder filter(String filterParam) {
            this.filterParam = filterParam;
            if (this.filterParam != null && !this.filterParam.isEmpty()) {
                if (this.selectParam == null) {
                    url += "?$filter=" + this.filterParam;
                } else {
                    url += "&$filter=" + this.filterParam;
                }
            }
            return this;
        }

        public String build() {
            if (selectParam == null && filterParam == null) {
                return url + "?$format=JSON";
            } else {
                return url + "&$format=JSON";
            }
        }

        public String buildTest() {
            if (selectParam == null && filterParam == null) {
                return url + "?$top=10&$format=JSON";
            } else {
                return url + "&$top=10&$format=JSON";
            }
        }
    }


}
