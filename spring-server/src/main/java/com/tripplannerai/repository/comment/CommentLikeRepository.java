package com.tripplannerai.repository.comment;

import com.tripplannerai.entity.comment.Comment;
import com.tripplannerai.entity.comment.CommentLike;
import com.tripplannerai.entity.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CommentLikeRepository extends JpaRepository<CommentLike,Long> {
    @Query("select cl from CommentLike cl left join cl.member m left join cl.comment c where c = :comment and m = :member")
    Optional<CommentLike> findByCommentAndMember(Comment comment, Member member);
}
