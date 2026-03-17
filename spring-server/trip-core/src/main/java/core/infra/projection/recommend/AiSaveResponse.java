package core.infra.projection.recommend;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AiSaveResponse {
    private String code;
    private String message;


    public static AiSaveResponse of(String code, String message) {

        return new AiSaveResponse(code, message);
    }
}
