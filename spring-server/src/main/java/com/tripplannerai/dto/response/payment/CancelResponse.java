package com.tripplannerai.dto.response.payment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CancelResponse {
    private String code;
    private String message;

    public static CancelResponse of(String code, String message) {
        return new CancelResponse(code, message);
    }
}
