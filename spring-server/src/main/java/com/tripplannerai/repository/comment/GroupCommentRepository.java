package com.tripplannerai.repository.comment;

import com.tripplannerai.dto.response.comment.CommentElement;
import com.tripplannerai.entity.comment.Comment;
import com.tripplannerai.entity.comment.GroupComment;
import com.tripplannerai.entity.comment.GroupCommentLike;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


public interface GroupCommentRepository extends JpaRepository<GroupComment, Long> {

    @Query("select new com.tripplannerai.dto.response.comment.CommentElement" +
            "(c.id,c.content,m.id,m.nickname,c.createdAt,c.updatedAt,c.isDeleted,c.count) " +
            "from GroupComment c " +
            "left join c.member m " +
            "left join c.group g " +
            "where g.groupId = :groupId")
    Page<CommentElement> findCommentsByGroup(Long groupId, Pageable pageable);



}
