package id.co.bni.cardbinding.models.microservice;

import com.fasterxml.jackson.annotation.JsonProperty;
import id.co.bni.cardbinding.exception.validation.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestRegistration {
    @NotNull
    @NotEmpty
    @AntiXXS
    @Pattern(regexp = "^(|[^<>{}\\[\\]()!^;'\"/:?]*)$", message = "Invalid patnerReferenceNo")
    @Size(min = 5, max = 200)
    private String partnerReferenceNo;
    @NotNull
    @NotEmpty
    @AntiXXS
    private String accountName;
    @AntiXXS
    private String cardData;
    @AntiXXS
    @NotNull
    private String custIdMerchant;
    @EnumAllowed(allowedValues = {"Y", "N"}, message = "The isBindAndPay field must be 'Y' or 'N'")
    @JsonProperty(defaultValue = "N")
    private String isBindAndPay;
    @AntiXXS
    private String merchantId;
    @AntiXXS
    private String terminalId;
    @AntiXXS
    private String journeyId;
    @AntiXXS
    private String subMerchantId;
    @AntiXXS
    private String externalStoreId;
    @PositiveNumber(message = "Only positive number is not allowed")
    private String limit;
    @Url
    private String merchantLogoUrl;
    @NotNull
    @Pattern(regexp = "^(|[^<>{}\\[\\]()!^;'\"/:?]*)$", message = "Invalid phoneNo")
    @AntiXXS
    private String phoneNo;
    @EnumAllowed(allowedValues = {"YES", "NO"}, message = "The sendOtpFlag field must be 'YES' or 'NO'")
    private String sendOtpFlag;
    @EnumAllowed(allowedValues = {"subscribe", "unsubscribe"}, message = "Invalid field type")
    private String type;
    private AdditionalInfo additionalInfo;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AdditionalInfo {
        @NotNull
        @AntiXXS
        private String deviceId;
        @NotNull
        @AntiXXS
        private String channel;
        @NotNull
        @Url
        private String urlCallbackSuccess;
    }
}
