package com.mockserver.service;

import com.mockserver.api.request.PaymentRequest;
import com.mockserver.api.response.ApiResponse;
import com.mockserver.api.response.PaymentData;
import com.mockserver.domain.entity.Payment;
import com.mockserver.repository.PaymentRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @InjectMocks
    private PaymentService paymentService;

    @Mock
    private PaymentRepository paymentRepository;

    @Spy
    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("결제 조회 성공")
    void payment_success() {
        Payment payment = Payment.builder()
                .id(1L)
                .paymentKey("pk_123")
                .orderId("order_1")
                .amount(10000L)
                .retry(0)
                .createTime(LocalDateTime.now())
                .build();
        given(paymentRepository.findByPaymentKey("pk_123")).willReturn(Optional.of(payment));

        ApiResponse<PaymentData> response = paymentService.payment("pk_123");

        assertThat(response.getData()).isNotNull();
        assertThat(response.getData().getPaymentKey()).isEqualTo("pk_123");
        assertThat(response.getData().getOrderId()).isEqualTo("order_1");
    }

    @Test
    @DisplayName("존재하지 않는 결제 조회 시 예외 발생")
    void payment_notFound() {
        given(paymentRepository.findByPaymentKey("not_exist")).willReturn(Optional.empty());

        assertThatThrownBy(() -> paymentService.payment("not_exist"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Not found payment!!");
    }

    @Test
    @DisplayName("결제 확인 시 Payment 저장")
    void confirm() {
        PaymentRequest request = PaymentRequest.builder()
                .paymentKey("pk_456")
                .orderId("order_2")
                .amount(20000L)
                .build();

        paymentService.confirm(request);

        then(paymentRepository).should().save(any(Payment.class));
    }
}
