package id.co.bni.cardbinding.models.cardlink;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResActivateCard {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private DataDTO data;
    private String message;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private IsoResponse isoResponse;
    private String responseMessage;
    private String responseCode;
    private String cardNumber;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DataDTO {
        private String cardOrg;
        private String expiryDate;
        private String firstDigits;
        private String cardType;
        @JsonProperty("isPosting")
        private boolean isPosting;
        private String lastDigits;
        private String custOrg;
        private String customerNumber;
        private String type;
        private String cardNumber;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class IsoResponse {
        private String responseMessage;
        private String responseCode;
    }
}
