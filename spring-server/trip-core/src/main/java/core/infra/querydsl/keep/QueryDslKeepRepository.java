package core.infra.querydsl.keep;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import core.infra.projection.keep.KeepElement;
import core.domain.entity.destination.QDestination;
import core.domain.entity.keep.Keep;
import core.domain.entity.keep.QKeep;
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
public class QueryDslKeepRepository {

    private final JPAQueryFactory queryFactory;

    public Page<KeepElement> fetchKeeps(Long id, Pageable pageable) {
        QKeep keep = QKeep.keep;
        QMember member = QMember.member;
        QDestination destination = QDestination.destination;

        List<KeepElement> content = queryFactory
                .select(Projections.constructor(KeepElement.class,
                        destination.contentId))
                .from(keep)
                .leftJoin(keep.member, member)
                .leftJoin(keep.destination, destination)
                .where(member.id.eq(id))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(keep.count())
                .from(keep)
                .leftJoin(keep.member, member)
                .where(member.id.eq(id))
                .fetchOne();

        return new PageImpl<>(content, pageable, total != null ? total : 0L);
    }

    public KeepElement fetchKeep(String contentId, Long id) {
        QKeep keep = QKeep.keep;
        QMember member = QMember.member;
        QDestination destination = QDestination.destination;

        return queryFactory
                .select(Projections.constructor(KeepElement.class,
                        destination.contentId))
                .from(keep)
                .leftJoin(keep.member, member)
                .leftJoin(keep.destination, destination)
                .where(member.id.eq(id)
                        .and(destination.contentId.eq(contentId)))
                .fetchOne();
    }

    public Optional<Keep> findByMemberAndDestination(String contentId, Long id) {
        QKeep keep = QKeep.keep;
        QMember member = QMember.member;
        QDestination destination = QDestination.destination;

        return Optional.ofNullable(queryFactory
                .selectFrom(keep)
                .leftJoin(keep.member, member)
                .leftJoin(keep.destination, destination)
                .where(member.id.eq(id)
                        .and(destination.contentId.eq(contentId)))
                .fetchFirst());
    }
}
