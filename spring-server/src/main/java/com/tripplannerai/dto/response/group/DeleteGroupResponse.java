package com.tripplannerai.dto.response.group;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DeleteGroupResponse {
    String code;
    String message;

    public static DeleteGroupResponse of(String code, String message) {
        return new  DeleteGroupResponse(code, message);
    }
}
