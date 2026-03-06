package com.tripplannerai.dto.response.comment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentResponse {
    private String code;
    private String message;
    private List<CommentElement> data;
    private boolean hasNext;

    public static CommentResponse of(String code, String message, List<CommentElement> comments, boolean hasNext) {
        return new CommentResponse(code,message,comments,hasNext);
    }
}
