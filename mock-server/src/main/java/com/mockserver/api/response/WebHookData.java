package com.mockserver.api.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WebHookData {
    private String status;
    private String paymentKey;
    private String orderId;
    private String totalAmount;
}
