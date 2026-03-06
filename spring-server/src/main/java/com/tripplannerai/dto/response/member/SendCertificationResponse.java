package com.tripplannerai.dto.response.member;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SendCertificationResponse {
    private String code;
    private String message;

    public static SendCertificationResponse of(String code, String message) {
        return new SendCertificationResponse(code, message);
    }
}
