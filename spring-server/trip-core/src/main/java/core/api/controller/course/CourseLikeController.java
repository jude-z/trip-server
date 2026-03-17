package core.api.controller.course;

import core.api.common.resolver.annotation.Id;
import core.api.response.ApiDataResponse;
import core.api.response.ApiResponse;
import core.api.response.ApiStatusResponse;
import core.common.Status;
import core.service.course.CourseLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/courseLike")
public class CourseLikeController {

    private final CourseLikeService courseLikeService;

    @PostMapping("/{courseId}")
    public ResponseEntity<ApiResponse> addLike(@PathVariable Long courseId, @Id Long userId) {
        courseLikeService.addLikeCourse(courseId, userId);
        return ResponseEntity.ok(ApiStatusResponse.of(Status.SUCCESS));
    }

    @DeleteMapping("/{courseId}")
    public ResponseEntity<ApiResponse> removeLike(@PathVariable Long courseId, @Id Long userId) {
        courseLikeService.removeLikeCourse(courseId, userId);
        return ResponseEntity.ok(ApiStatusResponse.of(Status.SUCCESS));
    }

    @GetMapping("/{courseId}/liked")
    public ResponseEntity<ApiResponse> isLiked(@PathVariable Long courseId, @Id Long userId) {
        return ResponseEntity.ok(ApiDataResponse.of(courseLikeService.isLiked(courseId, userId), Status.SUCCESS));
    }

    @GetMapping("/liked")
    public ResponseEntity<ApiResponse> getLikedCourses(@Id Long memberId) {
        return ResponseEntity.ok(courseLikeService.getLikedCourses(memberId));
    }
}
