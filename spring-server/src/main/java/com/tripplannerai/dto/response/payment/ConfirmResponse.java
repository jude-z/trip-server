package com.tripplannerai.dto.response.payment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ConfirmResponse {
    private String code;
    private String message;

    public static ConfirmResponse of(String code, String message) {
        return new ConfirmResponse(code, message);
    }
}
