package com.tripplannerai.dto.response.payment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SaveTempResponse {

    private String code;
    private String message;

    public static SaveTempResponse of(String code, String message){
        return new SaveTempResponse(code, message);
    }
}
