package payment.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import core.common.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {
    private String code;
    private String message;

    public static ErrorResponse of(Status status) {
        return new ErrorResponse(status.getCode(), status.getMessage());
    }
}
