package payment.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import core.api.security.basic.userdetail.CustomUserDetails;
import core.api.security.jwt.JwtSubject;
import core.common.Status;
import payment.api.common.resolver.IdResolver;
import payment.api.common.resolver.UsernameResolver;
import payment.api.request.PaymentRequest;
import payment.api.request.CancelPaymentRequest;
import payment.api.response.ApiStatusResponse;
import payment.service.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PaymentController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import({IdResolver.class, UsernameResolver.class})
class PaymentControllerTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @MockitoBean
    private PaymentService paymentService;

    @BeforeEach
    void setUp() {
        JwtSubject jwtSubject = JwtSubject.builder().id(1L).email("test@example.com").nickname("tester").build();
        CustomUserDetails userDetails = new CustomUserDetails(jwtSubject, List.of(new SimpleGrantedAuthority("ROLE_USER")));
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    @DisplayName("POST /api/confirm - success")
    void confirm_success() throws Exception {
        PaymentRequest paymentRequest = PaymentRequest.builder()
                .paymentKey("paykey_123")
                .orderId("order_123")
                .amount(10000)
                .build();

        given(paymentService.confirm(anyString(), any(PaymentRequest.class), anyLong()))
                .willReturn(ApiStatusResponse.of(Status.SUCCESS));

        mockMvc.perform(post("/api/confirm")
                        .header("Idempotency-Key", "idem-key-001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(paymentRequest)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /api/cancel - success")
    void cancel_success() throws Exception {
        CancelPaymentRequest cancelPaymentRequest = new CancelPaymentRequest("Customer requested cancellation");

        given(paymentService.cancel(anyString(), anyLong(), any(CancelPaymentRequest.class), anyString()))
                .willReturn(ApiStatusResponse.of(Status.SUCCESS));

        mockMvc.perform(post("/api/cancel")
                        .header("Idempotency-Key", "idem-key-002")
                        .param("paymentKey", "paykey_123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cancelPaymentRequest)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /api/payments - success")
    void fetchPayments_success() throws Exception {
        given(paymentService.fetchPayments(anyLong(), anyInt()))
                .willReturn(ApiStatusResponse.of(Status.SUCCESS));

        mockMvc.perform(get("/api/payments")
                        .param("pageNum", "1"))
                .andExpect(status().isOk());
    }
}
