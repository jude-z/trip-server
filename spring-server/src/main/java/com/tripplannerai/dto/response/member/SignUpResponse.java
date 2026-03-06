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
public class SignUpResponse {
    private String code;
    private String message;

    public static SignUpResponse of (String code, String message) {
        return new SignUpResponse(code, message);
    }
}
