package id.co.bni.cardbinding.models.cardlink;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResValidatePIN {
    private String phoneNumber;
    private String message;
    private String responseMessage;
    private String cardNumber;
    private String responseCode;
    private String bankCardToken;
    private String urlCallbackSuccess;
}
