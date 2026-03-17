package core.infra.querydsl.group;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import core.infra.projection.group.GroupElement;
import core.domain.entity.group.QGroup;
import core.domain.entity.member.QMember;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class QueryDslGroupRepository {

    private final JPAQueryFactory queryFactory;

    public Page<GroupElement> groups(Pageable pageable) {
        QGroup group = QGroup.group;
        QMember member = QMember.member;

        List<GroupElement> content = queryFactory
                .select(Projections.constructor(GroupElement.class,
                        group.groupId,
                        group.title,
                        group.description,
                        group.count,
                        group.participateCount,
                        group.groupLikeCount,
                        group.maxCount,
                        group.startDate,
                        group.endDate,
                        member.id))
                .from(group)
                .leftJoin(group.member, member)
                .where(group.status.eq(true))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(group.count())
                .from(group)
                .where(group.status.eq(true))
                .fetchOne();

        return new PageImpl<>(content, pageable, total != null ? total : 0L);
    }

    public Optional<GroupElement> fetchGroup(Long groupId) {
        QGroup group = QGroup.group;
        QMember member = QMember.member;

        GroupElement result = queryFactory
                .select(Projections.constructor(GroupElement.class,
                        group.groupId,
                        group.title,
                        group.description,
                        group.count,
                        group.participateCount,
                        group.groupLikeCount,
                        group.maxCount,
                        group.startDate,
                        group.endDate,
                        member.id))
                .from(group)
                .leftJoin(group.member, member)
                .where(group.groupId.eq(groupId))
                .fetchOne();

        return Optional.ofNullable(result);
    }
}
