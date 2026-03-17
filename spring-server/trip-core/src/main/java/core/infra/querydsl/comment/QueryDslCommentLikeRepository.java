package core.infra.querydsl.comment;

import com.querydsl.jpa.impl.JPAQueryFactory;
import core.domain.entity.comment.Comment;
import core.domain.entity.comment.CommentLike;
import core.domain.entity.comment.QCommentLike;
import core.domain.entity.member.Member;
import core.domain.entity.member.QMember;
import core.domain.entity.comment.QComment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class QueryDslCommentLikeRepository {

    private final JPAQueryFactory queryFactory;

    public Optional<CommentLike> findByCommentAndMember(Comment comment, Member member) {
        QCommentLike commentLike = QCommentLike.commentLike;
        return Optional.ofNullable(queryFactory
                .selectFrom(commentLike)
                .leftJoin(commentLike.member, QMember.member)
                .leftJoin(commentLike.comment, QComment.comment)
                .where(commentLike.comment.eq(comment)
                        .and(commentLike.member.eq(member)))
                .fetchFirst());
    }
}
