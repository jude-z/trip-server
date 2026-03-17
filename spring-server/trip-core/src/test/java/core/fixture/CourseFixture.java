package core.fixture;

import core.domain.entity.course.Course;
import core.domain.entity.courselike.CourseLike;
import core.domain.entity.member.Member;

import java.time.LocalDateTime;

public class CourseFixture {

    public static Course.CourseBuilder defaultCourse() {
        return Course.builder()
                .courseId(1L)
                .contentId("COURSE001")
                .title("Seoul City Tour")
                .overview("A beautiful tour around Seoul")
                .areaName("Seoul")
                .duration("3 hours")
                .distance("10km")
                .rating(4.5)
                .likeCount(10)
                .reviewCount(5);
    }

    public static Course createCourse() {
        return defaultCourse().build();
    }

    public static Course createCourseWithId(Long id) {
        return defaultCourse().courseId(id).build();
    }

    public static CourseLike.CourseLikeBuilder defaultCourseLike(Course course, Member member) {
        return CourseLike.builder()
                .id(1L)
                .course(course)
                .member(member)
                .likedAt(LocalDateTime.now());
    }

    public static CourseLike createCourseLike(Course course, Member member) {
        return defaultCourseLike(course, member).build();
    }
}
