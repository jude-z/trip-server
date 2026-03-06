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
public class RefreshResponse {
    private String code;
    private String message;
    private String accessToken;

    public static RefreshResponse of(String code, String message, String accessToken) {
        return new RefreshResponse(code, message, accessToken);
    }
}
