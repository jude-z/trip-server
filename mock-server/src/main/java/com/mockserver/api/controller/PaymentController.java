package com.mockserver.api.controller;

import com.mockserver.api.request.PaymentRequest;
import com.mockserver.api.response.ApiResponse;
import com.mockserver.api.response.PaymentData;
import com.mockserver.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    @GetMapping("/v1/payment/{paymentKey}")
    ResponseEntity<ApiResponse<?>> payment(@PathVariable("paymentKey") String paymentKey){
        ApiResponse<PaymentData> apiResponse = paymentService.payment(paymentKey);
        return ResponseEntity.ok(apiResponse);
    }
    @GetMapping("/v1/paymentCount/{paymentKey}")
    ResponseEntity<ApiResponse<?>> paymentCount(@PathVariable("paymentKey") String pamentKey){
        ApiResponse<Long> apiResponse = paymentService.paymentCount(pamentKey);
        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/v1/payment/confirm")
    ResponseEntity<ApiResponse<?>> confirm(@RequestBody PaymentRequest paymentRequest){
        paymentService.confirm(paymentRequest);
        return ResponseEntity.ok().build();
    }
}
