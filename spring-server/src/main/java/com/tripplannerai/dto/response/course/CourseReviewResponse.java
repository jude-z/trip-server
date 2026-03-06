package com.tripplannerai.dto.response.course;

import com.tripplannerai.entity.coursereview.CourseReview;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseReviewResponse {
    private String review;
    private int rating;
    private String writer;

    public static CourseReviewResponse fromEntity(CourseReview courseReview) {
        return CourseReviewResponse.builder()
                .review(courseReview.getReview())
                .rating(courseReview.getRating())
                .writer(courseReview.getMember().getNickname())
                .build();
    }
}
