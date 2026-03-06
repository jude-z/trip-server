package com.tripplannerai.dto.response.payment;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentElement {
    private Long paymentId;
    private String paymentKey;
    private Integer amount;
    private String orderId;
    private boolean cancelled;
}
