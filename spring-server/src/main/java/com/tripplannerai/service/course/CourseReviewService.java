package com.tripplannerai.service.course;

import com.tripplannerai.dto.request.course.CourseReviewRequest;

public interface CourseReviewService {
    void addReview(CourseReviewRequest request, Long memberId);
}
