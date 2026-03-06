package com.tripplannerai.dto.response.fail;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FailResponse {
    private String code;
    private String message;
    private String clientId;
    private String email;

    public static FailResponse of(String code, String message, String clientId, String email) {
        return new FailResponse(code, message, clientId, email);
    }
}
