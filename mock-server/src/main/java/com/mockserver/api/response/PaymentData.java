package com.mockserver.api.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentData {
    private String paymentKey;
    private String orderId;
    private Long totalAmount;
}
