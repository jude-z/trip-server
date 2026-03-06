package com.tripplannerai.dto.response.group;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeaveGroupResponse {
    private String code;
    private String message;

    public static LeaveGroupResponse of(String code, String message) {
        return new LeaveGroupResponse(code, message);
    }
}
