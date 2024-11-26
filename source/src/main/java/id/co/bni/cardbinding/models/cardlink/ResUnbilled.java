package id.co.bni.cardbinding.models.cardlink;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResUnbilled {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<Object> data;
    private String message;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String responseCode;
}
