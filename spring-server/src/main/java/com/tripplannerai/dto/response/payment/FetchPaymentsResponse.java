package com.tripplannerai.dto.response.payment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FetchPaymentsResponse {
    private String code;
    private String message;
    private List<PaymentElement> content;
    private boolean hasNext;

    public static FetchPaymentsResponse of(String code, String message, List<PaymentElement> content, boolean hasNext) {
        return new FetchPaymentsResponse(code, message, content, hasNext);
    }
}
