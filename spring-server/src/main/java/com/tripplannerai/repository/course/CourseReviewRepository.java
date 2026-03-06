package com.tripplannerai.repository.course;

import com.tripplannerai.entity.course.Course;
import com.tripplannerai.entity.coursereview.CourseReview;
import com.tripplannerai.entity.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourseReviewRepository extends JpaRepository<CourseReview, Long> {
    boolean existsByCourseAndMember(Course course, Member member);
    List<CourseReview> findByCourse(Course course);
}
