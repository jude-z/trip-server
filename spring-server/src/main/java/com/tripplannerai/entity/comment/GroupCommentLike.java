package com.tripplannerai.entity.comment;

import com.tripplannerai.entity.member.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupCommentLike {
    @Id
    @Column(name = "group_comment_like_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_comment_id")
    private GroupComment groupComment;

    public static GroupCommentLike of (GroupComment groupComment, Member member){
        return GroupCommentLike.builder()
                .groupComment(groupComment)
                .member(member)
                .build();
    }
}
