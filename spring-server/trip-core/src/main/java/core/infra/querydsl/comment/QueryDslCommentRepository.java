package core.infra.querydsl.comment;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import core.infra.projection.comment.CommentElement;
import core.domain.entity.comment.Comment;
import core.domain.entity.comment.QComment;
import core.domain.entity.member.QMember;
import core.domain.entity.receiptreview.QReceiptReview;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class QueryDslCommentRepository {

    private final JPAQueryFactory queryFactory;

    public Page<CommentElement> findCommentsByReceiptReview(Long reviewId, Pageable pageable) {
        QComment comment = QComment.comment;
        QMember member = QMember.member;
        QReceiptReview receiptReview = QReceiptReview.receiptReview;

        List<CommentElement> content = queryFactory
                .select(Projections.constructor(CommentElement.class,
                        comment.id,
                        comment.content,
                        member.id,
                        member.nickname,
                        comment.createdAt,
                        comment.updatedAt,
                        comment.isDeleted,
                        comment.count))
                .from(comment)
                .leftJoin(comment.member, member)
                .leftJoin(comment.receiptReview, receiptReview)
                .where(receiptReview.receiptReviewId.eq(reviewId)
                        .and(comment.isDeleted.eq(false)))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(comment.count())
                .from(comment)
                .leftJoin(comment.receiptReview, receiptReview)
                .where(receiptReview.receiptReviewId.eq(reviewId)
                        .and(comment.isDeleted.eq(false)))
                .fetchOne();

        return new PageImpl<>(content, pageable, total != null ? total : 0L);
    }

    public Comment findLastCommentByParentComment(Long parentCommentId) {
        QComment comment = QComment.comment;
        QComment parent = new QComment("parent");
        return queryFactory
                .selectFrom(comment)
                .leftJoin(parent).on(comment.parentComment.id.eq(parent.id))
                .fetchFirst();
    }

    public List<Long> findByRecursive(Long commentId) {
        QComment comment = QComment.comment;
        return queryFactory
                .select(comment.id)
                .from(comment)
                .where(comment.id.eq(commentId))
                .fetch();
    }
}
