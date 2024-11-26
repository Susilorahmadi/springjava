package id.co.bni.cardbinding.models.cardlink;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResSummary {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private DataDTO data;
    private String message;
    private String responseMessage;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String cardNumber;
    private String responseCode;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DataDTO {
        private String availableCash;
        private String originNumber;
        private String cashLimit;
        private String statusColor;
        private String statusKartuCode;
        private String onlinePaymentAmt;
        private String basicCardNumber;
        private String statementBalance;
        private String postingFlag;
        private String lastStatementDate;
        private String typeNumber;
        private String cashBalance;
        private String customerEmail;
        private String lastPaymentDate;
        @JsonProperty("isPosting")
        private boolean isPosting;
        private String creditLimit;
        private String lastPaymentAmount;
        private String customerDob;
        private String embossName;
        private String expiry;
        private String reasonCode;
        private String statusKartuDesc;
        private String email;
        private String coHomePhone;
        private String blockCode;
        private String memoBalance;
        private String availableCredit;
        private String delqHistory;
        private String currentBalance;
        private String totalAmountDue;
        private String custOrg;
        private String currentDue;
        private String customerNumber;
        private String customerName;
        private String openedDate;
        private String paymentDueDate;
        private String nextStatementDate;
        private String phoneNumber;
        private String statusKartu;
        private String availablePoint;
        private String cardNumber;
        private String status;
    }
}
