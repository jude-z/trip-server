package core.infra.jpa.course;

import core.domain.entity.course.Course;
import core.domain.entity.coursereview.CourseReview;
import core.domain.entity.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourseReviewRepository extends JpaRepository<CourseReview, Long> {
    boolean existsByCourseAndMember(Course course, Member member);
    List<CourseReview> findByCourse(Course course);
}
