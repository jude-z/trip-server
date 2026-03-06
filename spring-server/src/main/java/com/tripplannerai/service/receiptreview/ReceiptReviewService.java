package com.tripplannerai.service.receiptreview;

import com.tripplannerai.dto.request.receiptreview.ReceiptReviewRequest;
import com.tripplannerai.dto.response.receiptreview.ReceiptReviewResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ReceiptReviewService {
    void createReceiptReview(ReceiptReviewRequest request, List<MultipartFile> images, Long memberId) throws IOException;
    Page<ReceiptReviewResponse> getReviews(Pageable pageable);

    ReceiptReviewResponse getReview(Long id);
}
