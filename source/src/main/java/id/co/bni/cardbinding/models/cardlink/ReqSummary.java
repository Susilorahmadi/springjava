package id.co.bni.cardbinding.models.cardlink;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReqSummary {
    private String cardNumber;
    private String channelID;
}
