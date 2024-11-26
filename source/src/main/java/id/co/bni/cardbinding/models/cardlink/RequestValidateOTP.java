package id.co.bni.cardbinding.models.cardlink;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestValidateOTP {
    private String Channel;
    @JsonProperty(value = "phone_number")
    private String PhoneNumber;
    @JsonProperty(value = "country_code")
    private String countryCode;
    @JsonProperty(value = "otp_code")
    private String otpCode;
    @JsonProperty(value = "product_code")
    private String productCode;
}
