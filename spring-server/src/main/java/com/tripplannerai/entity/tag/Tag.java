package com.tripplannerai.entity.tag;

import com.tripplannerai.entity.course.Course;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "tag")
@NoArgsConstructor
@AllArgsConstructor
public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tagId;
    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;
    private String tagName;
}
