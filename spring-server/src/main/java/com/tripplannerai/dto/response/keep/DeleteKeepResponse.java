package com.tripplannerai.dto.response.keep;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class DeleteKeepResponse {
    private String code;
    private String message;

    public static DeleteKeepResponse of(String code, String message){
        return new DeleteKeepResponse(code, message);
    }
}
