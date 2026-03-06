package com.tripplannerai.dto.response.course;

import com.tripplannerai.entity.course.Course;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseInfoResponse {
    private Long courseId;
    private String contentId;
    private String title;
    private String areaName;
    private String overview;
    private String distance;
    private String duration;
    private int likeCount;
    private double rating;
    private int reviewCount;

    public static CourseInfoResponse fromEntity(Course course){
        return CourseInfoResponse.builder()
                .courseId(course.getCourseId())
                .contentId(course.getContentId())
                .title(course.getTitle())
                .areaName(course.getAreaName())
                .overview(course.getOverview())
                .distance(course.getDistance())
                .duration(course.getDuration())
                .likeCount(course.getLikeCount())
                .rating(course.getRating())
                .reviewCount(course.getReviewCount())
                .build();

    }
}
