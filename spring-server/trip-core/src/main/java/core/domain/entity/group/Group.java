package core.domain.entity.group;

import core.domain.entity.destination.Destination;
import core.domain.entity.member.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@Table(name = "groups")
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "group_id")
    private Long groupId;
    private String title;
    private boolean status;
    @Lob
    private String description;
    private int count;
    private int participateCount;
    private int groupLikeCount;
    @Column(name = "max_count")
    private int maxCount;
    @Column(name = "start_date")
    private LocalDateTime startDate;
    @Column(name = "end_date")
    private LocalDateTime endDate;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "destination_id")
    private Destination destination;
    @OneToOne
    @JoinColumn(name = "member_id")
    private Member member;

    public void plusCount(){
        this.count++;
    }
    public void minusCount(){
        this.count--;
    }
    public void plusParticipateCount(){
        this.participateCount++;
    }
    public void minusParticipateCount(){
        this.participateCount--;
    }
    public void plusGroupLikeCount(){
        this.groupLikeCount++;
    }
    public void minusGroupLikeCount(){
        this.groupLikeCount--;
    }


    public void changeStatus(boolean status) {
        this.status = status;
    }
}
