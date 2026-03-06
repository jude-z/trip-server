package com.tripplannerai.entity.receiptreviewimage;

import com.tripplannerai.entity.receiptreview.ReceiptReview;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReceiptReviewImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reviewImageId;
    private String imageUrl;
    @Column(name = "content_type")
    private String contentType;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="receipt_review_id")
    private ReceiptReview review;

    public void setReview(ReceiptReview review) {
        this.review = review;
    }
}
