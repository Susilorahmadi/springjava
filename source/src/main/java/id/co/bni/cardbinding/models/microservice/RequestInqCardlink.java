package id.co.bni.cardbinding.models.microservice;

import id.co.bni.cardbinding.exception.validation.EnumAllowed;
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
public class RequestInqCardlink {
    @NotNull
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
        private String channel;

        @StringToDate
        @Size(min=6, max = 12, message = "Invalid date format, expected yyyy-MM-dd")
        private String statementDate;

        @MustEmptyString
        private String authCode;

        @StringToDate
        @Size(min=6, max = 12, message = "Invalid date format, expected yyyy-MM-dd")
        private String dob;

        @Pattern(regexp =  "^(|[^<>{}\\[\\]()!^;'\"/:?]*)$", message = "Invalid expiryDate")
        private String expiryDate;

        @Pattern(regexp = "^(|[^<>{}\\[\\]()!^;'\"/:?]*)$", message = "Invalid OTP sender")
        @EnumAllowed(allowedValues = {"WHATSAPP", "SMS"}, message = "The otpSender field must be Whatsapp or SMS")
        private String otpSender;

        @MustEmptyString
        private String pin;

        @MustEmptyString
        private String otpCode;
    }

}