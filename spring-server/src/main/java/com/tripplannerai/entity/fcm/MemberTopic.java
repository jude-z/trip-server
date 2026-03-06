package com.tripplannerai.entity.fcm;

import com.tripplannerai.entity.member.Member;
import jakarta.persistence.*;

@Entity
public class MemberTopic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_topic")
    private Long memberTopicId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id")
    private Topic topic;
}
