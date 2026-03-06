package com.tripplannerai.dto.response.group;

import com.tripplannerai.entity.group.Group;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class AddGroupResponse {
    private String code;
    private String message;
    private Long groupId;

    public static AddGroupResponse of(String code, String message, Group group) {
        return new AddGroupResponse(code,message,group.getGroupId());
    }
}
