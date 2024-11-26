package id.co.bni.cardbinding.models.cardlink;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReqValidatePIN {
    private String cardNumber;
    private String expiryDate;
    private String pin;
    private String dob;
    private String channelID;
}
