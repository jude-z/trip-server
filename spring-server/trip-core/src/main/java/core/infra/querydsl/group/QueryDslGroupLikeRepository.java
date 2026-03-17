package core.infra.querydsl.group;

import com.querydsl.jpa.impl.JPAQueryFactory;
import core.domain.entity.group.Group;
import core.domain.entity.group.GroupLike;
import core.domain.entity.group.QGroup;
import core.domain.entity.group.QGroupLike;
import core.domain.entity.member.Member;
import core.domain.entity.member.QMember;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class QueryDslGroupLikeRepository {

    private final JPAQueryFactory queryFactory;

    public Optional<GroupLike> findByGroupAndMember(Group group, Member member) {
        QGroupLike groupLike = QGroupLike.groupLike;
        return Optional.ofNullable(queryFactory
                .selectFrom(groupLike)
                .leftJoin(groupLike.group, QGroup.group)
                .leftJoin(groupLike.member, QMember.member)
                .where(groupLike.group.eq(group)
                        .and(groupLike.member.eq(member)))
                .fetchFirst());
    }
}
