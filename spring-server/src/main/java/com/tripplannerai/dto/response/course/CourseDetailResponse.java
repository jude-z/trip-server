package com.tripplannerai.dto.response.course;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseDetailResponse {
    private Long courseId;
    private String title;
    private String overview;
    private List<CourseSpotDto> spots;
    private int likeCount;
    private double rating;
    private int reviewCount;
    private List<CourseReviewResponse> reviewList;


}
