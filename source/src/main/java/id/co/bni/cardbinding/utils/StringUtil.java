package id.co.bni.cardbinding.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.util.*;

@Component
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StringUtil {
    public static String setUuid() {
        return UUID.randomUUID().toString().replace("-", "").toUpperCase();
    }

    public String generateToken() {
        String token = UUID.randomUUID().toString().replaceAll("-", "");
        return token;
    }

    public static String toString(Object object) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.warn("message : {}", e.getMessage());
        }
        return "";
    }

    public static Map<String, Object> toMap(String str) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(str, new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            log.warn("message : {}", e.getMessage());
        }
        return Collections.emptyMap();
    }

    private static String buildQueryString(Map<String, String> params) {
        StringBuilder queryString = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (!queryString.isEmpty()) {
                queryString.append("&");
            }
            queryString.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8));
            queryString.append("=");
            queryString.append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8));
        }
        return queryString.toString();
    }

//    public static URI createUrl(String uri, Map<String, String> param) {
//        URI url = null;
//        try {
//            url = new URI(uri + "?" + buildQueryString(param));
//        } catch (URISyntaxException e) {
//            e.getMessage();
//        }
//        return url;
//    }

    public static List<String> pathToList(String path) {
        String[] segments = path.split("/");
        List<String> segmentList = Arrays.asList(segments);
        return segmentList.subList(1, segmentList.size());
    }

    public static String upperUnderScore(String str) {
        if (str.contains("-")) {
            return str.trim().toUpperCase().replace("-","_");
        }
        return str.trim().toUpperCase();
    }

    public static Timestamp getAsTimestamp() {
        try {
            return new Timestamp(System.currentTimeMillis());
        } catch (Exception e) {
            log.warn(e.getMessage());
        }
        return null;
    }
}
