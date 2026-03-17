package core.domain.entity.group;

import core.domain.entity.member.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@Table(name = "group_like")
public class GroupLike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "group__like_id")
    private Long groupLikeId;
    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;
    @ManyToOne
    @JoinColumn(name = "group_id")
    private Group group;

    public static GroupLike of(Member member, Group group){
        return GroupLike.builder()
                .member(member)
                .group(group)
                .build();
    }

}
