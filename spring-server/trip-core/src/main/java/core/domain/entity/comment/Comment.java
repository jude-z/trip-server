package core.domain.entity.comment;

import core.domain.entity.BaseEntity;
import core.domain.entity.member.Member;
import core.domain.entity.receiptreview.ReceiptReview;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comment extends BaseEntity {
    @Id
    @Column(name = "comment_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int count;
    private String content;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receipt_review_id")
    private ReceiptReview receiptReview;
    private boolean isDeleted;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_comment_id")
    private Comment parentComment;
    @OneToMany(fetch = FetchType.LAZY)
    private List<Comment> childComments;
    private String path;
    private int depth;

    public static Comment of(Member member, ReceiptReview receiptReview, String content,String path, Comment parentComment,int depth) {
        return Comment.builder()
                .member(member)
                .content(content)
                .receiptReview(receiptReview)
                .isDeleted(false)
                .path(path)
                .parentComment(parentComment)
                .childComments(new ArrayList<>())
                .depth(depth)
                .count(0)
                .build();
    }
    public void plusCount(){
        this.count++;
    }
    public void minusCount(){
        this.count--;
    }
    public void changeContent(String content){
        this.content = content;
    }
    public void changeStatus(boolean status){
        this.isDeleted = status;
    }

}
