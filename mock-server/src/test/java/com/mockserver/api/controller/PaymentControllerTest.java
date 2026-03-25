package com.mockserver.api.controller;

import com.mockserver.api.response.ApiResponse;
import com.mockserver.api.response.PaymentData;
import com.mockserver.service.PaymentService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PaymentController.class)
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PaymentService paymentService;

    @Test
    @DisplayName("GET /mock/payment/{paymentKey} - 결제 조회 성공")
    void payment_success() throws Exception {
        PaymentData paymentData = PaymentData.builder()
                .paymentKey("pk_123")
                .orderId("order_1")
                .totalAmount("10000")
                .build();
        ApiResponse<PaymentData> apiResponse = ApiResponse.<PaymentData>builder()
                .data(paymentData)
                .build();
        given(paymentService.payment("pk_123")).willReturn(apiResponse);

        mockMvc.perform(get("/mock/payment/{paymentKey}", "pk_123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.paymentKey").value("pk_123"))
                .andExpect(jsonPath("$.data.orderId").value("order_1"))
                .andExpect(jsonPath("$.data.totalAmount").value("10000"));
    }

    @Test
    @DisplayName("GET /mock/payment/{paymentKey} - 존재하지 않는 결제 조회 시 400")
    void payment_notFound() throws Exception {
        given(paymentService.payment("not_exist"))
                .willThrow(new RuntimeException("Not found payment!!"));

        mockMvc.perform(get("/mock/payment/{paymentKey}", "not_exist"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /mock/payment/confirm - 결제 확인 성공")
    void confirm_success() throws Exception {
        mockMvc.perform(post("/mock/payment/confirm")
                        .param("paymentKey", "pk_456")
                        .param("orderId", "order_2")
                        .param("amount", "20000"))
                .andExpect(status().isOk());

        then(paymentService).should().confirm(org.mockito.ArgumentMatchers.any());
    }
}
