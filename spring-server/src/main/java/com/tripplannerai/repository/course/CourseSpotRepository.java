package com.tripplannerai.repository.course;

import com.tripplannerai.entity.coursespot.CourseSpot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CourseSpotRepository extends JpaRepository<CourseSpot,Long> {
    @Query(value = "SELECT * FROM course_spot WHERE course_id = :courseId order by order_in_course ASC" , nativeQuery = true)
    List<CourseSpot> findByCourseId(@Param("courseId") Long courseId);
}
