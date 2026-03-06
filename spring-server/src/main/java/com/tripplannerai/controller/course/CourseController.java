package com.tripplannerai.controller.course;

import com.tripplannerai.dto.response.course.CourseDetailResponse;
import com.tripplannerai.dto.response.course.CourseResponse;
import com.tripplannerai.service.course.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/course")
public class CourseController {
    private final CourseService courseService;

    @GetMapping
    public ResponseEntity<List<CourseResponse>> getRecommendCourses() throws Exception {
        return ResponseEntity.ok(courseService.recommendCourses());
    }

    @GetMapping("/{courseId}")
    public ResponseEntity<CourseDetailResponse> getCourseDetail(@PathVariable Long courseId) {
        return ResponseEntity.ok(courseService.getCourseDetails(courseId));
    }


}
