package com.tripplannerai.mapper;

import com.tripplannerai.entity.enroll.Enroll;
import com.tripplannerai.entity.group.Group;
import com.tripplannerai.entity.member.Member;

public class EnrollFactory {
    public static Enroll from(Member member, Group group,boolean accepted) {
        return Enroll.builder()
                .member(member)
                .group(group)
                .accepted(accepted)
                .build();
    }
}
