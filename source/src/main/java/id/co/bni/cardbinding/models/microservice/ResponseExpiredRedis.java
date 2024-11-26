package id.co.bni.cardbinding.models.microservice;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseExpiredRedis {
    private String responseCode;
    private String responseMessage;
    private String authCode;
    private String state;
}
