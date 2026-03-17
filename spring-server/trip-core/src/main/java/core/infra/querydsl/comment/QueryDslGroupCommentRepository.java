package core.infra.querydsl.comment;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import core.infra.projection.comment.CommentElement;
import core.domain.entity.comment.QGroupComment;
import core.domain.entity.group.QGroup;
import core.domain.entity.member.QMember;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class QueryDslGroupCommentRepository {

    private final JPAQueryFactory queryFactory;

    public Page<CommentElement> findCommentsByGroup(Long groupId, Pageable pageable) {
        QGroupComment groupComment = QGroupComment.groupComment;
        QMember member = QMember.member;
        QGroup group = QGroup.group;

        List<CommentElement> content = queryFactory
                .select(Projections.constructor(CommentElement.class,
                        groupComment.id,
                        groupComment.content,
                        member.id,
                        member.nickname,
                        groupComment.createdAt,
                        groupComment.updatedAt,
                        groupComment.isDeleted,
                        groupComment.count))
                .from(groupComment)
                .leftJoin(groupComment.member, member)
                .leftJoin(groupComment.group, group)
                .where(group.groupId.eq(groupId))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(groupComment.count())
                .from(groupComment)
                .leftJoin(groupComment.group, group)
                .where(group.groupId.eq(groupId))
                .fetchOne();

        return new PageImpl<>(content, pageable, total != null ? total : 0L);
    }
}
