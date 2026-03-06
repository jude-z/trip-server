package com.tripplannerai.dto.response.member;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class EmailCheckoutResponse {
    private String code;
    private String message;

    public static EmailCheckoutResponse of(String code,String message) {
        return new EmailCheckoutResponse(code,message);
    }
}
