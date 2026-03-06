package com.tripplannerai.dto.response.course;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseResponse {
    private Long courseId;
    private String contentId;
    private String thumbnailUrl;
    private String title;
    private String area;
    private int likeCount;
    private double rating;
    private int reviewCount;


}
