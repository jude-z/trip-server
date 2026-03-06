package com.tripplannerai.entity.receiptreview;

import com.tripplannerai.entity.member.Member;
import com.tripplannerai.entity.receiptreviewimage.ReceiptReviewImage;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReceiptReview {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long receiptReviewId;
    private String address;
    private String title;
    @Column(length = 3000)
    private String content;
    private int rating;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="member_id")
    private Member member;   // 작성자

    @Column(name = "created_at", updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Builder.Default
    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReceiptReviewImage> images = new ArrayList<>();

    public void addImage(ReceiptReviewImage image) {
        images.add(image);
        image.setReview(this);
    }

}
