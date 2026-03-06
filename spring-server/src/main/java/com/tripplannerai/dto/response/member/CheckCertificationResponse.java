package com.tripplannerai.dto.response.member;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CheckCertificationResponse {
    private String code;
    private String message;

    public static CheckCertificationResponse of(String code, String message) {
        return new CheckCertificationResponse(code, message);
    }
}
