package id.co.bni.cardbinding.models.microservice;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReqCallbackApi {
    private String bankCardToken;
    private String authCode;
    private String clientId;
    private String url;
    private String phoneNo;
}
