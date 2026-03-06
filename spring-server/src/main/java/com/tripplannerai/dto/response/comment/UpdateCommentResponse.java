package com.tripplannerai.dto.response.comment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateCommentResponse {
    private String code;
    private String message;
    public static UpdateCommentResponse of(String code, String message){
        return new UpdateCommentResponse(code, message);
    }
}
