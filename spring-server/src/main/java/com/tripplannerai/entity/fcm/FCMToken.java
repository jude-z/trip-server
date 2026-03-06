package com.tripplannerai.entity.fcm;

import com.tripplannerai.entity.member.Member;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class FCMToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "fcm_token_id")
    private Long fcmTokenId;
    @Column(name = "token_value")
    private String tokenValue;
    @Column(name = "expiration_date")
    private LocalDateTime expirationDate;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;
}
