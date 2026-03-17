package core.infra.jpa.course;

import core.domain.entity.course.Course;
import core.domain.entity.courselike.CourseLike;
import core.domain.entity.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CourseLikeRepository extends JpaRepository<CourseLike,Long> {
    Optional<CourseLike> findByCourseAndMember(Course course, Member member);
    boolean existsByCourseAndMember(Course course, Member member);
}
