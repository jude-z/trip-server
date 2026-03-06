package com.tripplannerai.dto.response.keep;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class CreateKeepResponse {
    private String code;
    private String message;

    public static CreateKeepResponse of(String code, String message){
        return new CreateKeepResponse(code, message);
    }
}
