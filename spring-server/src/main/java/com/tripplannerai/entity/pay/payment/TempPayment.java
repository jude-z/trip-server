package com.tripplannerai.entity.pay.payment;

import com.tripplannerai.entity.member.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TempPayment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "temp_payment_id")
    private Long tempPaymentId;
    private String orderId;
    private Integer amount;
    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;
}
