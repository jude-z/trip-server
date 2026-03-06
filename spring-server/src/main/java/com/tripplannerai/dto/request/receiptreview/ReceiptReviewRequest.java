package com.tripplannerai.dto.request.receiptreview;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReceiptReviewRequest {
    private String address;
    private String title;
    private String content;
    private Integer rating;
}
