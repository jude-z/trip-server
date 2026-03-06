package com.tripplannerai.dto.response.payment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CheckTempResponse {
    private String code;
    private String message;

    public static CheckTempResponse of(String code, String message){
        return new CheckTempResponse(code, message);
    }
}
