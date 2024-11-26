package id.co.bni.cardbinding.models.cardlink;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReqActivateCard {
    private String cardNumber;
    private String dob;
    private String phone;
    private String channelID;
}
