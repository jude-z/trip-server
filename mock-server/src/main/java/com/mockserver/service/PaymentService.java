package com.mockserver.service;

import com.mockserver.api.request.PaymentRequest;
import com.mockserver.api.response.ApiResponse;
import com.mockserver.api.response.PaymentData;
import com.mockserver.domain.entity.Payment;
import com.mockserver.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final ObjectMapper objectMapper;

    public ApiResponse<PaymentData> payment(String paymentKey) {
        Payment payment = paymentRepository.findByPaymentKey(paymentKey)
                .orElseThrow(() -> new RuntimeException("Not found payment!!"));
        PaymentData paymentData = objectMapper.convertValue(payment, PaymentData.class);
        paymentData.setTotalAmount(payment.getAmount());
        return ApiResponse.<PaymentData>builder()
                .data(paymentData)
                .build();
    }

    public ApiResponse<Long> paymentCount(String paymentKey){
        Long paymentCount = paymentRepository.countByPaymentKey(paymentKey);
        return ApiResponse.<Long>builder()
                .data(paymentCount)
                .build();
    }

    public void confirm(PaymentRequest paymentRequest) {
        String paymentKey = paymentRequest.getPaymentKey();
        String orderId = paymentRequest.getOrderId();
        Long amount = paymentRequest.getAmount();

        paymentRepository.save(Payment.from(paymentKey,orderId,amount));
    }
}
