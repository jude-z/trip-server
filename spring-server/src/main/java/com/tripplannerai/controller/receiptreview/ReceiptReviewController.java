package com.tripplannerai.controller.receiptreview;

import com.tripplannerai.common.annotation.Id;
import com.tripplannerai.dto.request.receiptreview.ReceiptReviewRequest;
import com.tripplannerai.dto.response.receiptreview.ReceiptReviewResponse;
import com.tripplannerai.service.receiptreview.ReceiptReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/receiptReview")
public class ReceiptReviewController {
    private final ReceiptReviewService receiptReviewService;

    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Void> addReceiptReview(
            @RequestPart("review") ReceiptReviewRequest request,
            @RequestPart(value = "images", required = false) List<MultipartFile> images,
            @Id Long memberId
    ) throws IOException {
        receiptReviewService.createReceiptReview(request,images,memberId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/reviews")
    public ResponseEntity<Page<ReceiptReviewResponse>> getReviews(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(receiptReviewService.getReviews(pageable));
    }

    @GetMapping("/reviews/{id}")
    public ResponseEntity<ReceiptReviewResponse> getReview(@PathVariable Long id) {
        return ResponseEntity.ok(receiptReviewService.getReview(id));
    }





}
