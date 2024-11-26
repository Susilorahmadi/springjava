package id.co.bni.cardbinding.models.microservice;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseInqSummary {
    private String statusKartu;
    private String blockCode;
    private String credentialNo;
    private String creditLimit;
    private String memoBalance;
    private String availableCredit;
    private String statementDate;
    private String message;
    private String responseMessage;
    private String cardNumber;
    private String responseCode;
}
