package com.mockserver.domain.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String paymentKey;
    private String orderId;
    private Long amount;
    private Integer retry;
    private LocalDateTime createTime;


    public static Payment from(String paymentKey, String orderId, Long amount) {
        return Payment.builder()
                .paymentKey(paymentKey)
                .orderId(orderId)
                .amount(amount)
                .retry(0)
                .createTime(LocalDateTime.now())
                .build();
    }

    public void increaseRetry() {
        this.retry++;
    }
}
