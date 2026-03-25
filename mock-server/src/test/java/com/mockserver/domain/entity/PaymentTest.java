package com.mockserver.domain.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class PaymentTest {

    @Test
    @DisplayName("Payment.from()으로 생성 시 retry는 0, createTime은 현재 시간")
    void from() {
        Payment payment = Payment.from("pk_123", "order_1", 10000L);

        assertThat(payment.getPaymentKey()).isEqualTo("pk_123");
        assertThat(payment.getOrderId()).isEqualTo("order_1");
        assertThat(payment.getAmount()).isEqualTo(10000L);
        assertThat(payment.getRetry()).isZero();
        assertThat(payment.getCreateTime()).isBeforeOrEqualTo(LocalDateTime.now());
    }

    @Test
    @DisplayName("increaseRetry 호출 시 retry가 1씩 증가")
    void increaseRetry() {
        Payment payment = Payment.from("pk_123", "order_1", 10000L);

        payment.increaseRetry();
        assertThat(payment.getRetry()).isEqualTo(1);

        payment.increaseRetry();
        assertThat(payment.getRetry()).isEqualTo(2);

        payment.increaseRetry();
        assertThat(payment.getRetry()).isEqualTo(3);
    }
}
