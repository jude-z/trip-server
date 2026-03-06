package com.tripplannerai.dto.response.receiptreview;

import com.tripplannerai.entity.receiptreviewimage.ReceiptReviewImage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewImageDto {
    private Long reviewImageId;
    private String imageUrl;
    public static ReviewImageDto fromEntity(ReceiptReviewImage image) {
        return new ReviewImageDto(image.getReviewImageId(), image.getImageUrl());
    }
}
