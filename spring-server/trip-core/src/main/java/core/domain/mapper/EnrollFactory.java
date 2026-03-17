package core.domain.mapper;

import core.domain.entity.enroll.Enroll;
import core.domain.entity.group.Group;
import core.domain.entity.member.Member;

public class EnrollFactory {
    public static Enroll from(Member member, Group group,boolean accepted) {
        return Enroll.builder()
                .member(member)
                .group(group)
                .accepted(accepted)
                .build();
    }
}
