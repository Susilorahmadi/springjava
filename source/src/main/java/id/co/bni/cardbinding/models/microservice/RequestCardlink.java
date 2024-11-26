package id.co.bni.cardbinding.models.microservice;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestCardlink {
    private String data;
    @Size(max = 687)
    private String cardNumber;
    private FieldAdditionalInfo additionalInfo;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FieldAdditionalInfo {
        private String channel;
        private String statementDate;
        private String authCode;
        private String dob;
        private String expiryDate;
        private String otpSender;
        private String pin;
        private String otpCode;
    }
}
