package payment.api.response;

import core.common.Status;
import lombok.Builder;

public class ApiDataResponse<T> extends ApiResponse{
    private T data;
    private String code;
    private String message;

    @Builder
    private ApiDataResponse(T data, String code, String message){
        this.data = data;
        this.code = code;
        this.message = message;
    }

    public static <T> ApiDataResponse<T> of(T data, Status status){
        String code = status.getCode();
        String message = status.getMessage();
        return ApiDataResponse.<T>builder()
                .data(data)
                .code(code)
                .message(message)
                .build();
    }



}
