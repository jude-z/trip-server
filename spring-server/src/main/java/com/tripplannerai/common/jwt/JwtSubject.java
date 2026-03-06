package com.tripplannerai.common.jwt;

import com.tripplannerai.entity.member.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JwtSubject {
    private Long id;
    private String email;
    private String nickname;
    private String  url;

    public static JwtSubject of(Member member){
        return new JwtSubject(member.getId(),member.getEmail(),
                member.getNickname(),
                member.getImage() != null ? member.getImage().getUrl() : null);
    }
}
