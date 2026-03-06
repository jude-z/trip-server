package com.tripplannerai.dto.response.group;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DonateResponse {

    private String code;
    private String message;

    public static DonateResponse of(String code, String message) {
        return new DonateResponse(code, message);
    }
}
