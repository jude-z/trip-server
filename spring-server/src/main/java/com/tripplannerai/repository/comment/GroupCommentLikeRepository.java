package com.tripplannerai.repository.comment;

import com.tripplannerai.entity.comment.GroupComment;
import com.tripplannerai.entity.comment.GroupCommentLike;
import com.tripplannerai.entity.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface GroupCommentLikeRepository extends JpaRepository<GroupCommentLike, Long> {


    Optional<GroupCommentLike> findByGroupCommentAndMember(GroupComment groupComment, Member member);
}
