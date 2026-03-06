package com.tripplannerai.dto.response.receiptreview;

import com.tripplannerai.entity.receiptreview.ReceiptReview;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReceiptReviewResponse {
    private Long receiptReviewId;
    private String title;
    private String content;
    private int rating;
    private LocalDateTime createdAt;
    private String nickname;
    private List<ReviewImageDto> images;

    public static ReceiptReviewResponse fromEntity(ReceiptReview review) {
        return ReceiptReviewResponse.builder()
                .receiptReviewId(review.getReceiptReviewId())
                .title(review.getTitle())
                .content(review.getContent())
                .rating(review.getRating())
                .createdAt(review.getCreatedAt())
                .nickname(review.getMember().getNickname()) // 작성자 닉네임 세팅
                .images(review.getImages().stream()
                        .map(ReviewImageDto::fromEntity)
                        .toList())
                .build();
    }


}
