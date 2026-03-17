package payment.api.response;

import payment.api.response.ApiResponse;
import core.common.Status;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ApiStatusResponse extends ApiResponse {
    private String code;
    private String message;

    @Builder
    private ApiStatusResponse(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public static ApiStatusResponse of(Status status) {
        return ApiStatusResponse.builder()
                .code(status.getCode())
                .message(status.getMessage())
                .build();
    }
}
