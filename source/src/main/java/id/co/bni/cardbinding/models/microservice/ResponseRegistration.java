package id.co.bni.cardbinding.models.microservice;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseRegistration {
    private String responseCode;
    private String responseMessage;
    private String referenceNo;
    private String partnerReferenceNo;
    private String bankCardToken;
    private String chargeToken;
    private String randomString;
    private String tokenExpiryTime;
    private AdditionalInfo additionalInfo;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AdditionalInfo {
        private String deviceId;
        private String channel;
        private String authCode;
        private String state;
        private String urlBindingPage;
    }
}
