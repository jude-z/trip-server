package com.tripplannerai.dto.response.member;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SignInResponse {
    private String code;
    private String message;
    private String accessToken;

    public static SignInResponse of(String code, String message, String accessToken) {
        return new SignInResponse(code, message, accessToken);
    }
}
