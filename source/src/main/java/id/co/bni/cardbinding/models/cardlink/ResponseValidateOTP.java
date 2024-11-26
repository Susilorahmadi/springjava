package id.co.bni.cardbinding.models.cardlink;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseValidateOTP {
    private String channel;
    @JsonProperty("phone_number")
    private String phoneNumber;
    @JsonProperty("country_code")
    private String countryCode;
    @JsonProperty("otp_code")
    private String otpCode;
    @JsonProperty("product_code")
    private String productCode;
    @JsonProperty("otp_validate_failed_attempt")
    private String otpValidateFailedAttempt;
    @JsonProperty("error_code")
    private String errorCode;
    @JsonProperty("error_message")
    private String errorMessage;
    private String status;
}
