package core.api.controller.course;

import core.api.common.resolver.annotation.Id;
import core.api.request.course.CourseReviewRequest;
import core.api.response.ApiResponse;
import core.api.response.ApiStatusResponse;
import core.common.Status;
import core.service.course.CourseReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/courseReview")
public class CourseReviewController {
    private final CourseReviewService courseReviewService;

    @PostMapping
    public ResponseEntity<ApiResponse> addReview(@RequestBody CourseReviewRequest request, @Id Long memberId) {
        courseReviewService.addReview(request, memberId);
        return ResponseEntity.ok(ApiStatusResponse.of(Status.SUCCESS));
    }
}
