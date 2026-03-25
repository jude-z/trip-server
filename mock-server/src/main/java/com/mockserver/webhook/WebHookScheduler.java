package com.mockserver.webhook;

import com.mockserver.api.response.ApiResponse;
import com.mockserver.api.response.WebHookData;
import com.mockserver.api.response.WebHookStatus;
import com.mockserver.domain.entity.Payment;
import com.mockserver.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebHookScheduler {

    private final PaymentRepository paymentRepository;
    private final RestTemplate restTemplate;

    @Value("${webhook.url}")
    private String webhookUrl;

    @Scheduled(scheduler = "webhookTaskScheduler", fixedDelay = 30000)
    @Transactional
    public void webHook() {
        LocalDateTime now = LocalDateTime.now();
        List<Payment> payments = paymentRepository.findRetryTargets(
                now.minusMinutes(10),
                now.minusMinutes(30),
                now.minusMinutes(60)
        );
        if(!payments.isEmpty()) log.info("payment exist!!");
        for (Payment payment : payments) {
            payment.increaseRetry();
            WebHookData webHookData = WebHookData.builder()
                    .status(WebHookStatus.DONE.getStatus())
                    .paymentKey(payment.getPaymentKey())
                    .orderId(payment.getOrderId())
                    .totalAmount(String.valueOf(payment.getAmount()))
                    .build();

            ApiResponse<WebHookData> body = ApiResponse.<WebHookData>builder()
                    .data(webHookData)
                    .build();

            HttpHeaders headers = new HttpHeaders();
            HttpEntity<ApiResponse<WebHookData>> request = new HttpEntity<>(body, headers);

            try {
                restTemplate.postForEntity(webhookUrl, request, Void.class);
                log.info("웹훅 전송 성공: paymentKey={}", payment.getPaymentKey());
            } catch (Exception e) {
                payment.increaseRetry();
                paymentRepository.save(payment);
                log.warn("웹훅 전송 실패 (retry={}): paymentKey={}", payment.getRetry(), payment.getPaymentKey());
            }
        }
    }
}
