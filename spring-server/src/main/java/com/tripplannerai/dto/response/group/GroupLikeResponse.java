package com.tripplannerai.dto.response.group;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GroupLikeResponse {
    private String code;
    private String message;

    public static GroupLikeResponse of(String code, String message) {
        return new GroupLikeResponse(code, message);
    }
}
