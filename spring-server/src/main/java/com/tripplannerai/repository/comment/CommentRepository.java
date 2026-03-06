package com.tripplannerai.repository.comment;

import com.tripplannerai.dto.response.comment.CommentElement;
import com.tripplannerai.entity.comment.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;


public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Query("select new com.tripplannerai.dto.response.comment.CommentElement" +
            "(c.id,c.content,m.id,m.nickname,c.createdAt,c.updatedAt,c.isDeleted,c.count) " +
            "from Comment c " +
            "left join c.member m " +
            "left join c.receiptReview r " +
            "where r.receiptReviewId = :reviewId and c.isDeleted = false")
    Page<CommentElement> findCommentsByReceiptReview(Long reviewId, Pageable pageable);

    @Query("select c from Comment c left join Comment p on c.parentComment.id = p.id")
    Comment findLastCommentByParentComment(Long parentCommentId);

    @Query("update Comment c set c.isDeleted = true where c.id in :ids")
    @Modifying
    void deleteAllById(List<Long> ids);

    @Query(value = "select comment_id from Comment where comment_id = :commentId",nativeQuery = true)
    List<Long> findByRecursive(Long commentId);

}
