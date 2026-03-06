package com.tripplannerai.dto.response.comment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddCommentResponse {
    private String code;
    private String message;
    public static  AddCommentResponse of(String code, String message){
        return new AddCommentResponse(code, message);
    }
}
