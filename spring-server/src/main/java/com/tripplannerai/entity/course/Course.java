package com.tripplannerai.entity.course;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long courseId;
    private String contentId;
    private String title;

    @Column(columnDefinition = "text")
    private String overview;
    private String areaName;
    private String duration; //예상체류시간
    private String distance; //총 코스 거리

    @Builder.Default
    @ColumnDefault("0.0")
    private Double rating = 0.0;

    @Builder.Default
    @ColumnDefault("0")
    private Integer likeCount=0;

    @Builder.Default
    @ColumnDefault("0")
    private Integer reviewCount=0;

}
