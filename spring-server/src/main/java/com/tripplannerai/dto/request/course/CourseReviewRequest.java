package com.tripplannerai.dto.request.course;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class CourseReviewRequest {
    private String review;
    private Long courseId;
    private Long rating;
}
