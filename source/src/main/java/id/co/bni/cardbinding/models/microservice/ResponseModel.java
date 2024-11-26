package id.co.bni.cardbinding.models.microservice;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseModel {
    private Boolean isSuccess;
    private Object payload;
    private String responseCode;
    private String responseMessage;
}
