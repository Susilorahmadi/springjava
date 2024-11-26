package id.co.bni.cardbinding.models.cardlink;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestSendOTP {
    private String Channel;
    @JsonProperty(value = "phone_number")
    private String PhoneNumber;
    @JsonProperty(value = "country_code")
    private String countryCode;
    @JsonProperty(value = "otp_sender")
    private String otpSender;
    @JsonProperty(value = "product_code")
    private String productCode;
}
