package com.tripplannerai.dto.response.member;

import com.tripplannerai.entity.member.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class UpdateResponse {
    private String code;
    private String message;
    private String email;

    public static UpdateResponse of(String code, String message, Member member) {
        return new UpdateResponse(code,message,String.valueOf(member.getEmail()));
    }
}
