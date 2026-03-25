package com.mockserver.webhook;

import com.mockserver.domain.entity.Payment;
import com.mockserver.repository.PaymentRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class WebHookSchedulerTest {

    @InjectMocks
    private WebHookScheduler webHookScheduler;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private RestTemplate restTemplate;

    private static final String WEBHOOK_URL = "http://localhost:8080/payment/webhook";

    private void setWebhookUrl() {
        ReflectionTestUtils.setField(webHookScheduler, "webhookUrl", WEBHOOK_URL);
    }

    @Test
    @DisplayName("전송 대상이 없으면 웹훅을 보내지 않음")
    void webHook_noTargets() {
        setWebhookUrl();
        given(paymentRepository.findRetryTargets(any(), any(), any()))
                .willReturn(Collections.emptyList());

        webHookScheduler.webHook();

        then(restTemplate).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("웹훅 전송 성공 시 Payment 삭제")
    void webHook_success() {
        setWebhookUrl();
        Payment payment = Payment.builder()
                .id(1L)
                .paymentKey("pk_123")
                .orderId("order_1")
                .amount(10000L)
                .retry(0)
                .createTime(LocalDateTime.now().minusMinutes(15))
                .build();

        given(paymentRepository.findRetryTargets(any(), any(), any()))
                .willReturn(List.of(payment));
        given(restTemplate.postForEntity(anyString(), any(), eq(Void.class)))
                .willReturn(ResponseEntity.ok().build());

        webHookScheduler.webHook();

        then(paymentRepository).should().delete(payment);
        then(paymentRepository).should(never()).save(any());
    }

    @Test
    @DisplayName("웹훅 전송 실패 시 retry 증가 후 저장")
    void webHook_failure_increaseRetry() {
        setWebhookUrl();
        Payment payment = Payment.builder()
                .id(1L)
                .paymentKey("pk_456")
                .orderId("order_2")
                .amount(20000L)
                .retry(0)
                .createTime(LocalDateTime.now().minusMinutes(15))
                .build();

        given(paymentRepository.findRetryTargets(any(), any(), any()))
                .willReturn(List.of(payment));
        given(restTemplate.postForEntity(anyString(), any(), eq(Void.class)))
                .willThrow(new RestClientException("Connection refused"));

        webHookScheduler.webHook();

        assertThat(payment.getRetry()).isEqualTo(1);
        then(paymentRepository).should().save(payment);
        then(paymentRepository).should(never()).delete(any());
    }

    @Test
    @DisplayName("여러 결제 중 일부 성공, 일부 실패 처리")
    void webHook_partialSuccess() {
        setWebhookUrl();
        Payment success = Payment.builder()
                .id(1L)
                .paymentKey("pk_success")
                .orderId("order_1")
                .amount(10000L)
                .retry(0)
                .createTime(LocalDateTime.now().minusMinutes(15))
                .build();

        Payment fail = Payment.builder()
                .id(2L)
                .paymentKey("pk_fail")
                .orderId("order_2")
                .amount(20000L)
                .retry(1)
                .createTime(LocalDateTime.now().minusMinutes(35))
                .build();

        given(paymentRepository.findRetryTargets(any(), any(), any()))
                .willReturn(List.of(success, fail));
        given(restTemplate.postForEntity(anyString(), any(), eq(Void.class)))
                .willReturn(ResponseEntity.ok().build())
                .willThrow(new RestClientException("timeout"));

        webHookScheduler.webHook();

        then(paymentRepository).should().delete(success);
        assertThat(fail.getRetry()).isEqualTo(2);
        then(paymentRepository).should().save(fail);
    }
}
