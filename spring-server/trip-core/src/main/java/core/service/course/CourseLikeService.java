package core.service.course;

import core.infra.projection.course.CourseInfoResponse;
import core.domain.entity.course.Course;
import core.domain.entity.courselike.CourseLike;
import core.domain.entity.member.Member;
import core.infra.jpa.course.CourseLikeRepository;
import core.infra.jpa.course.CourseRepository;
import core.infra.jpa.member.MemberRepository;
import core.infra.querydsl.course.QueryDslCourseLikeRepository;
import core.infra.jdbc.course.JdbcCourseRepository;
import core.api.response.ApiResponse;
import core.api.response.ApiDataResponse;
import core.common.Status;
import core.common.exception.CommonException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseLikeService {
    private final MemberRepository memberRepository;
    private final CourseRepository courseRepository;
    private final CourseLikeRepository courseLikeRepository;
    private final QueryDslCourseLikeRepository queryDslCourseLikeRepository;
    private final JdbcCourseRepository jdbcCourseRepository;
    @Transactional
    public void addLikeCourse(Long courseId, Long userId) {
        Course course = courseRepository.findById(courseId).orElseThrow(() -> new CommonException(Status.NOT_FOUND_COURSE));
        Member member = memberRepository.findById(userId).orElseThrow(() -> new CommonException(Status.NOT_FOUND_MEMBER));
        boolean alreadyLiked = courseLikeRepository.existsByCourseAndMember(course,member);
        if (alreadyLiked) {
            throw new CommonException(Status.ALREADY_LIKED_COURSE);
        }
        CourseLike courseLike = CourseLike.builder()
                .course(course)
                .member(member)
                .build();
        courseLikeRepository.save(courseLike);
        jdbcCourseRepository.updateLikeCount(1,courseId);
    }

    @Transactional
    public void removeLikeCourse(Long courseId, Long userId) {
        Course course = courseRepository.findById(courseId).orElseThrow(() -> new CommonException(Status.NOT_FOUND_COURSE));
        Member member = memberRepository.findById(userId).orElseThrow(() -> new CommonException(Status.NOT_FOUND_MEMBER));
        CourseLike courseLike = courseLikeRepository.findByCourseAndMember(course, member).orElseThrow(()-> new CommonException(Status.NOT_FOUND_COURSE_LIKE));
        courseLikeRepository.delete(courseLike);
        jdbcCourseRepository.updateLikeCount(-1,courseId);
    }

    public boolean isLiked(Long courseId, Long userId) {
        Course course = courseRepository.findById(courseId).orElseThrow(() -> new CommonException(Status.NOT_FOUND_COURSE));
        Member member = memberRepository.findById(userId).orElseThrow(() -> new CommonException(Status.NOT_FOUND_MEMBER));
        return courseLikeRepository.existsByCourseAndMember(course,member);
    }

    public ApiResponse getLikedCourses(Long memberId) {
        List<Long> courseIds = queryDslCourseLikeRepository.findCourseIdsByMemberId(memberId);
        List<Course> courseList = courseRepository.findAllById(courseIds);
        List<CourseInfoResponse> courseInfoResponseList = courseList.stream()
                .map(CourseInfoResponse::fromEntity)
                .toList();
        return ApiDataResponse.of(courseInfoResponseList, Status.GET_COURSES_LIKED);

    }


}
