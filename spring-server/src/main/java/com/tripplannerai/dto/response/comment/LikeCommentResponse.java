package com.tripplannerai.dto.response.comment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LikeCommentResponse {
    private String code;
    private String message;

    public static LikeCommentResponse of(String code, String message) {
        return new LikeCommentResponse(code,message);
    }
}
