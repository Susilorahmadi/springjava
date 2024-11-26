package id.co.bni.cardbinding.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.co.bni.cardbinding.utils.HandlerUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

@Service
@Slf4j
public class PageService {
    public ResponseEntity<Object> helperPage(String form){
        ObjectMapper mapper = new ObjectMapper();
        try {
            InputStream inputStream = PageService.class.getResourceAsStream("/json/"+form+".json");
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            Map<String, Object> jsonMap = mapper.readValue(reader, Map.class);
            return ResponseEntity.ok(jsonMap);
        }catch (Exception ex){
            return HandlerUtil.generalError(null);
        }
    }
}
