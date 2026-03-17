package core.api.controller.course;

import core.api.response.ApiResponse;
import core.service.course.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/course")
public class CourseController {
    private final CourseService courseService;

    @GetMapping
    public ResponseEntity<ApiResponse> getRecommendCourses() throws Exception {
        return ResponseEntity.ok(courseService.recommendCourses());
    }

    @GetMapping("/{courseId}")
    public ResponseEntity<ApiResponse> getCourseDetail(@PathVariable Long courseId) {
        return ResponseEntity.ok(courseService.getCourseDetails(courseId));
    }
}
