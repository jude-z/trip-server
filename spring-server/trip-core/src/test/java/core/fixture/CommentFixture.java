package core.fixture;

import core.domain.entity.comment.Comment;
import core.domain.entity.comment.CommentLike;
import core.domain.entity.member.Member;
import core.domain.entity.receiptreview.ReceiptReview;

public class CommentFixture {

    public static Comment.CommentBuilder defaultComment(Member member, ReceiptReview review) {
        return Comment.builder()
                .id(1L)
                .count(0)
                .content("Test comment")
                .member(member)
                .receiptReview(review)
                .isDeleted(false)
                .path("00001")
                .depth(0);
    }

    public static Comment createComment(Member member, ReceiptReview review) {
        return defaultComment(member, review).build();
    }

    public static Comment createChildComment(Member member, ReceiptReview review, Comment parent) {
        return Comment.builder()
                .id(2L)
                .count(0)
                .content("Reply comment")
                .member(member)
                .receiptReview(review)
                .isDeleted(false)
                .parentComment(parent)
                .path("00001/00001")
                .depth(1)
                .build();
    }

    public static CommentLike createCommentLike(Comment comment, Member member) {
        return CommentLike.of(comment, member);
    }

    public static ReceiptReview.ReceiptReviewBuilder defaultReceiptReview(Member member) {
        return ReceiptReview.builder()
                .receiptReviewId(1L)
                .address("Seoul, Korea")
                .title("Great place!")
                .content("Review content")
                .rating(5)
                .member(member);
    }

    public static ReceiptReview createReceiptReview(Member member) {
        return defaultReceiptReview(member).build();
    }
}
