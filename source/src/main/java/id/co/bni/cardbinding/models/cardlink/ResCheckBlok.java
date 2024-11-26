package id.co.bni.cardbinding.models.cardlink;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResCheckBlok {
    private DataDTO data;
    private String message;
    private IsoResponse isoResponse;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DataDTO {
        private String blockCode;
        private String statusKartu;
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
