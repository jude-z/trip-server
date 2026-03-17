package core.service.receiptreview;

import core.service.s3.S3UploadService;
import core.api.request.receiptreview.ReceiptReviewRequest;
import core.api.response.ApiResponse;
import core.api.response.ApiDataResponse;
import core.infra.projection.receiptreview.ReceiptReviewResponse;
import core.domain.entity.member.Member;
import core.domain.entity.receiptreview.ReceiptReview;
import core.domain.entity.receiptreviewimage.ReceiptReviewImage;
import core.common.Status;
import core.common.exception.CommonException;
import core.infra.jpa.member.MemberRepository;
import core.infra.jpa.receiptreview.ReceiptReviewRepository;
import core.infra.querydsl.receiptreview.QueryDslReceiptReviewRepository;
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
public class ReceiptReviewService {
    private final ReceiptReviewRepository reviewRepository;
    private final S3UploadService s3UploadService;
    private final MemberRepository memberRepository;
    private final QueryDslReceiptReviewRepository queryDslReceiptReviewRepository;

    @Transactional
    public void createReceiptReview(ReceiptReviewRequest request, List<MultipartFile> images, Long memberId) throws IOException {
        Member member = memberRepository.findById(memberId).orElseThrow(()->new CommonException(Status.NOT_FOUND_MEMBER));

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

    public ApiResponse getReviews(Pageable pageable) {
        Page<ReceiptReview> reviews = queryDslReceiptReviewRepository.findAllWithImagesAndMember(pageable);
        return ApiDataResponse.of(reviews.map(ReceiptReviewResponse::fromEntity), Status.SUCCESS);
    }

    public ApiResponse getReview(Long id) {
        ReceiptReview review = queryDslReceiptReviewRepository.findByIdWithImagesAndMember(id)
                .orElseThrow(() -> new CommonException(Status.NOT_FOUND_RECEIPT_REVIEW));
        return ApiDataResponse.of(ReceiptReviewResponse.fromEntity(review), Status.SUCCESS);
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
