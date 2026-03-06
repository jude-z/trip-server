package com.tripplannerai.service.receiptreview;

import com.tripplannerai.common.exception.member.NotFoundMemberException;
import com.tripplannerai.common.s3.S3UploadService;
import com.tripplannerai.dto.request.receiptreview.ReceiptReviewRequest;
import com.tripplannerai.dto.response.receiptreview.ReceiptReviewResponse;
import com.tripplannerai.entity.member.Member;
import com.tripplannerai.entity.receiptreview.ReceiptReview;
import com.tripplannerai.entity.receiptreviewimage.ReceiptReviewImage;
import com.tripplannerai.repository.member.MemberRepository;
import com.tripplannerai.repository.receiptreview.ReceiptReviewRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReceiptReviewServiceImpl implements ReceiptReviewService{
    private final ReceiptReviewRepository reviewRepository;
    private final S3UploadService s3UploadService;
    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public void createReceiptReview(ReceiptReviewRequest request, List<MultipartFile> images, Long memberId) throws IOException {
        Member member = memberRepository.findById(memberId).orElseThrow(()->new NotFoundMemberException("not found member"));

        ReceiptReview review = ReceiptReview.builder()
                .address(request.getAddress())
                .title(request.getTitle())
                .member(member)
                .rating(request.getRating())
                .content(request.getContent())
                .build();
        if(images != null){
            for (MultipartFile file : images) {
                String imageUrl = s3UploadService.uploadReceiptReviewImage(file);
                String originalFilename = file.getOriginalFilename();
                String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
                ReceiptReviewImage image = ReceiptReviewImage.builder()
                        .imageUrl(imageUrl)
                        .review(review)
                        .contentType(determineContentType(extension))
                        .build();
                review.addImage(image);
            }
        }
        reviewRepository.save(review);
    }

    @Override
    public Page<ReceiptReviewResponse> getReviews(Pageable pageable) {
        Page<ReceiptReview> reviews = reviewRepository.findAllWithImagesAndMember(pageable);
        return reviews.map(ReceiptReviewResponse::fromEntity);
    }

    @Override
    public ReceiptReviewResponse getReview(Long id) {
        ReceiptReview review = reviewRepository.findByIdWithImagesAndMember(id)
                .orElseThrow(() -> new EntityNotFoundException("리뷰를 찾을 수 없습니다."));
        return ReceiptReviewResponse.fromEntity(review);
    }

    private String determineContentType(String extension) {
        switch (extension.toLowerCase()) {
            case ".jpg":
            case ".jpeg":
                return "image/jpeg";
            case ".png":
                return "image/png";
            case ".gif":
                return "image/gif";
            default:
                return "application/octet-stream";
        }
    }
}
