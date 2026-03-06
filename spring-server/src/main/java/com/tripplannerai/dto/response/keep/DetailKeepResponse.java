package com.tripplannerai.dto.response.keep;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class DetailKeepResponse {
    private String code;
    private String message;
    private KeepElement data;

    public static DetailKeepResponse of(String code, String message,KeepElement keepElement){
        return new DetailKeepResponse(code, message,keepElement);
    }
}
