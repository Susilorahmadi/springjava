package id.co.bni.cardbinding.models.microservice;

import id.co.bni.cardbinding.exception.validation.AntiXXS;
import id.co.bni.cardbinding.exception.validation.MustEmptyString;
import id.co.bni.cardbinding.exception.validation.StringToDate;
import jakarta.validation.Valid;
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
public class RequestInqCardlinkSummary {
    @NotNull
    @AntiXXS
    @Size(min = 5,max = 150)
    @Pattern(regexp =  "^(|[^<>{}\\[\\]()!^;'\"/:?]*)$", message = "Invalid bankCardToken")
    private String bankCardToken;

    @NotNull
    @Valid
    private FieldAdditionalInfo additionalInfo;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FieldAdditionalInfo {
        @NotNull
        @Size(min = 2,max = 8)
        @AntiXXS
        @Pattern(regexp = "^(|[^<>{}\\[\\]()!^;'\"/:?]*)$", message = "Invalid channel")
        private String channel;

        @AntiXXS
        @StringToDate
        private String statementDate;

        @AntiXXS
        private String authCode;

        @AntiXXS
        @StringToDate
        private String dob;

        @AntiXXS
        private String expiryDate;

        @AntiXXS
        private String otpSender;

        @AntiXXS
        private String pin;

        @AntiXXS
        private String otpCode;
    }

}