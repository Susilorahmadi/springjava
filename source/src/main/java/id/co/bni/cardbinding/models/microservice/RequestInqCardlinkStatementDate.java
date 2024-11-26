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
public class RequestInqCardlinkStatementDate {
    @NotNull
    @AntiXXS
    @Size(min = 5,max = 64)
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
        @Pattern(regexp = "^(|[^<>{}\\[\\]()!^;'\"/:?]*)$", message = "Invalid channel")
        @Size(min = 2,max = 8)
        @AntiXXS
        private String channel;

        @NotNull
        @StringToDate
        @Size(min=6, max = 12, message = "Invalid date format, expected yyyy-MM-dd")
        private String statementDate;

        @AntiXXS
        private String authCode;

        @AntiXXS
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