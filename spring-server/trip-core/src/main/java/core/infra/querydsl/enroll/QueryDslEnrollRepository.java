package core.infra.querydsl.enroll;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import core.infra.projection.group.ApplyElement;
import core.domain.entity.enroll.Enroll;
import core.domain.entity.enroll.QEnroll;
import core.domain.entity.group.Group;
import core.domain.entity.group.QGroup;
import core.domain.entity.member.Member;
import core.domain.entity.member.QMember;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class QueryDslEnrollRepository {

    private final JPAQueryFactory queryFactory;

    public Optional<Enroll> findByMemberAndGroupAndAccepted(Member member, Group group) {
        QEnroll enroll = QEnroll.enroll;
        return Optional.ofNullable(queryFactory
                .selectFrom(enroll)
                .leftJoin(enroll.member, QMember.member)
                .leftJoin(enroll.group, QGroup.group)
                .where(enroll.member.eq(member)
                        .and(enroll.group.eq(group)))
                .fetchFirst());
    }

    public List<ApplyElement> findByGroupAndApply(Group group) {
        QEnroll enroll = QEnroll.enroll;
        QMember member = QMember.member;
        return queryFactory
                .select(Projections.constructor(ApplyElement.class,
                        enroll.enrollId,
                        member.nickname,
                        enroll.accepted))
                .from(enroll)
                .leftJoin(enroll.group, QGroup.group)
                .leftJoin(enroll.member, member)
                .where(enroll.accepted.eq(false))
                .fetch();
    }

    public List<ApplyElement> findByGroupAndParticipate(Group group) {
        QEnroll enroll = QEnroll.enroll;
        QMember member = QMember.member;
        return queryFactory
                .select(Projections.constructor(ApplyElement.class,
                        enroll.enrollId,
                        member.nickname,
                        enroll.accepted))
                .from(enroll)
                .leftJoin(enroll.group, QGroup.group)
                .leftJoin(enroll.member, member)
                .where(enroll.accepted.eq(true))
                .fetch();
    }
}
