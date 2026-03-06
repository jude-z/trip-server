package com.tripplannerai.service.course;

import com.tripplannerai.dto.response.course.CourseDetailResponse;
import com.tripplannerai.dto.response.course.CourseResponse;

import java.util.List;

public interface CourseService {
    void saveCourseData() throws Exception;
    List<CourseResponse> recommendCourses();
    CourseDetailResponse getCourseDetails(Long courseId);

}
