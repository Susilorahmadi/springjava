package id.co.bni.cardbinding.services;

import id.co.bni.cardbinding.configs.MicroserviceEnum;
import id.co.bni.cardbinding.exception.APIException;
import id.co.bni.cardbinding.exception.GeneralException;
import id.co.bni.cardbinding.models.microservice.ResponseModel;
import id.co.bni.cardbinding.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.*;

import java.util.List;

@Component
@Slf4j
public class RestService {
    private final RestTemplate restTemplate;

    public RestService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void startRequest(String method , String url, Object entity) {
        log.info("======== OUTGOING  REQUEST  ==========");
        log.info("Destination : {} {}", method, url);
        log.info("Payload     : {}", StringUtil.toString(entity));
    }
    private void endRequest(long end, long start) {
        long elapsedTimeInMillis = (end - start) / 1_000_000;
        String elapsedTime;
        if (elapsedTimeInMillis > 1000) {
            elapsedTime = (elapsedTimeInMillis / 1000) + " seconds";
        } else {
            elapsedTime = elapsedTimeInMillis + " ms";
        }
        log.info("======== INCOMING  RESPONSE ========== "  + elapsedTime);
    }
    public ResponseModel callAPI(String endpoint, String method, Object request) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Object> entity = new HttpEntity<>(request, headers);
        long start = System.nanoTime();

        ResponseModel responseModel = new ResponseModel();
        try {
            ResponseEntity<Object> response = restTemplate.exchange(endpoint, HttpMethod.valueOf(method), entity, Object.class);
            long end = System.nanoTime();
            endRequest(end, start);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                responseModel.setIsSuccess(true);
                responseModel.setPayload(response.getBody());
                responseModel.setResponseCode(MicroserviceEnum.SUCCESS.getResponseCode());
                responseModel.setResponseMessage(MicroserviceEnum.SUCCESS.getMessage());
                return responseModel;
            }
        } catch (RestClientResponseException e) {
            if (!e.getStatusCode().is2xxSuccessful()) {
                log.warn(e.getResponseBodyAsString());
                throw new APIException(e.getStatusCode(),e.getStatusText()+""+e.getStatusCode().value()+""+endpoint,e.getStatusCode().value()+":"+e.getResponseBodyAsString());
            }
        } catch (Exception e) {
            if(e.getMessage().contains("timed out")){
                log.warn(e.getMessage());
                throw new APIException(HttpStatus.BAD_GATEWAY,"(Aggregator) "+ e.getMessage(),"Read timed out");
            }
            throw new GeneralException("(Aggregator) "+e.getMessage());
        }
        return null;
    }
}
