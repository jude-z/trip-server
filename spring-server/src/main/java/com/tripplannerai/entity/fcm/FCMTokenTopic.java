package com.tripplannerai.entity.fcm;

import jakarta.persistence.*;

@Entity
public class FCMTokenTopic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "fcm_token_topic_id")
    private Long fcmTokenTopicId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fcm_token_id")
    private FCMToken fcmToken;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id")
    private Topic topic;
}
