package com.mockserver.repository;

import com.mockserver.domain.entity.Payment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class PaymentRepositoryTest {

    @Autowired
    private PaymentRepository paymentRepository;

    @BeforeEach
    void setUp() {
        paymentRepository.deleteAll();
    }

    @Test
    @DisplayName("paymentKey로 결제 조회")
    void findByPaymentKey() {
        Payment payment = Payment.builder()
                .id(1L)
                .paymentKey("pk_test")
                .orderId("order_1")
                .amount(5000L)
                .retry(0)
                .createTime(LocalDateTime.now())
                .build();
        paymentRepository.save(payment);

        Optional<Payment> found = paymentRepository.findByPaymentKey("pk_test");

        assertThat(found).isPresent();
        assertThat(found.get().getOrderId()).isEqualTo("order_1");
        assertThat(found.get().getAmount()).isEqualTo(5000L);
    }

    @Test
    @DisplayName("존재하지 않는 paymentKey 조회 시 빈 Optional 반환")
    void findByPaymentKey_notFound() {
        Optional<Payment> found = paymentRepository.findByPaymentKey("not_exist");

        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("retry=0이고 10분 경과한 결제만 조회")
    void findRetryTargets_retry0() {
        LocalDateTime now = LocalDateTime.now();

        Payment target = Payment.builder()
                .id(1L)
                .paymentKey("pk_old")
                .orderId("order_1")
                .amount(1000L)
                .retry(0)
                .createTime(now.minusMinutes(15))
                .build();

        Payment tooRecent = Payment.builder()
                .id(2L)
                .paymentKey("pk_new")
                .orderId("order_2")
                .amount(2000L)
                .retry(0)
                .createTime(now.minusMinutes(5))
                .build();

        paymentRepository.saveAll(List.of(target, tooRecent));

        List<Payment> results = paymentRepository.findRetryTargets(
                now.minusMinutes(10),
                now.minusMinutes(30),
                now.minusMinutes(60)
        );

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getPaymentKey()).isEqualTo("pk_old");
    }

    @Test
    @DisplayName("retry=1이고 30분 경과한 결제만 조회")
    void findRetryTargets_retry1() {
        LocalDateTime now = LocalDateTime.now();

        Payment target = Payment.builder()
                .id(1L)
                .paymentKey("pk_retry1")
                .orderId("order_1")
                .amount(1000L)
                .retry(1)
                .createTime(now.minusMinutes(35))
                .build();

        Payment tooRecent = Payment.builder()
                .id(2L)
                .paymentKey("pk_retry1_recent")
                .orderId("order_2")
                .amount(2000L)
                .retry(1)
                .createTime(now.minusMinutes(20))
                .build();

        paymentRepository.saveAll(List.of(target, tooRecent));

        List<Payment> results = paymentRepository.findRetryTargets(
                now.minusMinutes(10),
                now.minusMinutes(30),
                now.minusMinutes(60)
        );

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getPaymentKey()).isEqualTo("pk_retry1");
    }

    @Test
    @DisplayName("retry=2이고 60분 경과한 결제만 조회")
    void findRetryTargets_retry2() {
        LocalDateTime now = LocalDateTime.now();

        Payment target = Payment.builder()
                .id(1L)
                .paymentKey("pk_retry2")
                .orderId("order_1")
                .amount(1000L)
                .retry(2)
                .createTime(now.minusMinutes(65))
                .build();

        Payment tooRecent = Payment.builder()
                .id(2L)
                .paymentKey("pk_retry2_recent")
                .orderId("order_2")
                .amount(2000L)
                .retry(2)
                .createTime(now.minusMinutes(45))
                .build();

        paymentRepository.saveAll(List.of(target, tooRecent));

        List<Payment> results = paymentRepository.findRetryTargets(
                now.minusMinutes(10),
                now.minusMinutes(30),
                now.minusMinutes(60)
        );

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getPaymentKey()).isEqualTo("pk_retry2");
    }

    @Test
    @DisplayName("retry=3 이상은 조회되지 않음")
    void findRetryTargets_maxRetryExceeded() {
        LocalDateTime now = LocalDateTime.now();

        Payment exhausted = Payment.builder()
                .id(1L)
                .paymentKey("pk_exhausted")
                .orderId("order_1")
                .amount(1000L)
                .retry(3)
                .createTime(now.minusMinutes(120))
                .build();

        paymentRepository.save(exhausted);

        List<Payment> results = paymentRepository.findRetryTargets(
                now.minusMinutes(10),
                now.minusMinutes(30),
                now.minusMinutes(60)
        );

        assertThat(results).isEmpty();
    }
}
