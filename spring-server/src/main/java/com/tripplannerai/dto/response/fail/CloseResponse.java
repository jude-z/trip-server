package com.tripplannerai.dto.response.fail;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CloseResponse {

    private String code;
    private String message;

    public static CloseResponse of(String code, String message) {
        return new CloseResponse(code, message);
    }
}
