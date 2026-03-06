package com.tripplannerai.entity.member;

import com.tripplannerai.entity.BaseEntity;
import com.tripplannerai.entity.group.Group;
import com.tripplannerai.entity.image.Image;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "member")
public class Member extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(unique = true, nullable = false, length = 100)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(length = 20)
    private String nickname;

    @Column(length = 13)
    private String phoneNumber;

    @Column(nullable = true, length = 10)
    private String provider;

    @Column(nullable = true, length = 50)
    private String providerId;

    @Column(name = "state", columnDefinition = "BOOLEAN DEFAULT FALSE")  //회원탈퇴여부
    private boolean isWithdrawn;

    @Column(length = 2048)
    private String refreshToken;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name ="image_id")
    private Image image;


    @Column(name = "customer_key")
    private String customerKey;

    @Column
    private Integer ticket;




}
