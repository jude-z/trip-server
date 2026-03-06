package com.tripplannerai.entity.fcm;

import jakarta.persistence.*;

@Entity
public class Topic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "topic_id")
    private Long topicId;
    @Column(name = "topic_name")
    private String topicName;
}
