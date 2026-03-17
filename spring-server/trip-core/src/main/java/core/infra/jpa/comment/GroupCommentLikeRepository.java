package core.infra.jpa.comment;

import core.domain.entity.comment.GroupComment;
import core.domain.entity.comment.GroupCommentLike;
import core.domain.entity.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface GroupCommentLikeRepository extends JpaRepository<GroupCommentLike, Long> {


    Optional<GroupCommentLike> findByGroupCommentAndMember(GroupComment groupComment, Member member);
}
