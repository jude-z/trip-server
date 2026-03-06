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
public class ApplyGroupResponse {
    private String code;
    private String message;
    private List<ApplyElement> content;

    public static ApplyGroupResponse of(String code, String message, List<ApplyElement> content) {
        return new ApplyGroupResponse(code, message, content);
    }
}
