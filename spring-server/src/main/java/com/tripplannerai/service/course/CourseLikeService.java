package com.tripplannerai.service.course;

import com.tripplannerai.dto.response.course.CourseInfoResponse;

import java.util.List;

public interface CourseLikeService {
    void addLikeCourse(Long courseId, Long userId);
    void removeLikeCourse(Long courseId, Long userId);

    boolean isLiked(Long courseId, Long userId);
    List<CourseInfoResponse> getLikedCourses(Long memberId);
}
