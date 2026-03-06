package com.tripplannerai.entity.viewlog;

import com.tripplannerai.entity.BaseEntity;
import com.tripplannerai.entity.destination.Destination;
import com.tripplannerai.entity.member.Member;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "view_log")
public class ViewLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "view_log_id")
    private Long viewLogId;
    @Column(name = "view_log_date")
    private LocalDateTime viewLogDate;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "destination_id")
    private Destination destination;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;
}
