package com.tripplannerai.dto.response.course;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseSpotDto {
    private String contentId;
    private String description;
    private int order;
    private String title;
    private String address;
    private String imageUrl;
}
