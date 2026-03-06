package com.tripplannerai.controller.course;

import com.tripplannerai.common.annotation.Id;
import com.tripplannerai.dto.request.course.CourseReviewRequest;
import com.tripplannerai.service.course.CourseReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<Void> addReview(@RequestBody CourseReviewRequest request, @Id Long memberId) {
        courseReviewService.addReview(request,memberId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
