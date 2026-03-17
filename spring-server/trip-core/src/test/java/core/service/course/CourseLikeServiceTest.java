package core.service.course;

import core.api.response.ApiResponse;
import core.common.Status;
import core.common.exception.CommonException;
import core.domain.entity.course.Course;
import core.domain.entity.courselike.CourseLike;
import core.domain.entity.member.Member;
import core.fixture.CourseFixture;
import core.fixture.MemberFixture;
import core.infra.jdbc.course.JdbcCourseRepository;
import core.infra.jpa.course.CourseLikeRepository;
import core.infra.jpa.course.CourseRepository;
import core.infra.jpa.member.MemberRepository;
import core.infra.querydsl.course.QueryDslCourseLikeRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CourseLikeServiceTest {

    @InjectMocks
    private CourseLikeService courseLikeService;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private CourseLikeRepository courseLikeRepository;

    @Mock
    private QueryDslCourseLikeRepository queryDslCourseLikeRepository;

    @Mock
    private JdbcCourseRepository jdbcCourseRepository;

    @Test
    @DisplayName("addLikeCourse - success")
    void addLikeCourse_success() {
        // given
        Course course = CourseFixture.createCourse();
        Member member = MemberFixture.create();

        given(courseRepository.findById(1L)).willReturn(Optional.of(course));
        given(memberRepository.findById(1L)).willReturn(Optional.of(member));
        given(courseLikeRepository.existsByCourseAndMember(course, member)).willReturn(false);
        given(courseLikeRepository.save(any(CourseLike.class))).willAnswer(inv -> inv.getArgument(0));

        // when
        courseLikeService.addLikeCourse(1L, 1L);

        // then
        verify(courseLikeRepository).save(any(CourseLike.class));
        verify(jdbcCourseRepository).updateLikeCount(1, 1L);
    }

    @Test
    @DisplayName("addLikeCourse - already liked throws ALREADY_LIKED_COURSE")
    void addLikeCourse_alreadyLiked() {
        // given
        Course course = CourseFixture.createCourse();
        Member member = MemberFixture.create();

        given(courseRepository.findById(1L)).willReturn(Optional.of(course));
        given(memberRepository.findById(1L)).willReturn(Optional.of(member));
        given(courseLikeRepository.existsByCourseAndMember(course, member)).willReturn(true);

        // when & then
        assertThatThrownBy(() -> courseLikeService.addLikeCourse(1L, 1L))
                .isInstanceOf(CommonException.class)
                .satisfies(ex -> assertThat(((CommonException) ex).getStatus()).isEqualTo(Status.ALREADY_LIKED_COURSE));
    }

    @Test
    @DisplayName("addLikeCourse - course not found throws NOT_FOUND_COURSE")
    void addLikeCourse_courseNotFound() {
        // given
        given(courseRepository.findById(1L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> courseLikeService.addLikeCourse(1L, 1L))
                .isInstanceOf(CommonException.class)
                .satisfies(ex -> assertThat(((CommonException) ex).getStatus()).isEqualTo(Status.NOT_FOUND_COURSE));
    }

    @Test
    @DisplayName("removeLikeCourse - success")
    void removeLikeCourse_success() {
        // given
        Course course = CourseFixture.createCourse();
        Member member = MemberFixture.create();
        CourseLike courseLike = CourseLike.builder().course(course).member(member).build();

        given(courseRepository.findById(1L)).willReturn(Optional.of(course));
        given(memberRepository.findById(1L)).willReturn(Optional.of(member));
        given(courseLikeRepository.findByCourseAndMember(course, member)).willReturn(Optional.of(courseLike));

        // when
        courseLikeService.removeLikeCourse(1L, 1L);

        // then
        verify(courseLikeRepository).delete(courseLike);
        verify(jdbcCourseRepository).updateLikeCount(-1, 1L);
    }

    @Test
    @DisplayName("removeLikeCourse - not found throws NOT_FOUND_COURSE_LIKE")
    void removeLikeCourse_notFound() {
        // given
        Course course = CourseFixture.createCourse();
        Member member = MemberFixture.create();

        given(courseRepository.findById(1L)).willReturn(Optional.of(course));
        given(memberRepository.findById(1L)).willReturn(Optional.of(member));
        given(courseLikeRepository.findByCourseAndMember(course, member)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> courseLikeService.removeLikeCourse(1L, 1L))
                .isInstanceOf(CommonException.class)
                .satisfies(ex -> assertThat(((CommonException) ex).getStatus()).isEqualTo(Status.NOT_FOUND_COURSE_LIKE));
    }

    @Test
    @DisplayName("isLiked - returns true when liked")
    void isLiked_true() {
        // given
        Course course = CourseFixture.createCourse();
        Member member = MemberFixture.create();

        given(courseRepository.findById(1L)).willReturn(Optional.of(course));
        given(memberRepository.findById(1L)).willReturn(Optional.of(member));
        given(courseLikeRepository.existsByCourseAndMember(course, member)).willReturn(true);

        // when
        boolean result = courseLikeService.isLiked(1L, 1L);

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("isLiked - returns false when not liked")
    void isLiked_false() {
        // given
        Course course = CourseFixture.createCourse();
        Member member = MemberFixture.create();

        given(courseRepository.findById(1L)).willReturn(Optional.of(course));
        given(memberRepository.findById(1L)).willReturn(Optional.of(member));
        given(courseLikeRepository.existsByCourseAndMember(course, member)).willReturn(false);

        // when
        boolean result = courseLikeService.isLiked(1L, 1L);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("getLikedCourses - success")
    void getLikedCourses_success() {
        // given
        List<Long> courseIds = List.of(1L, 2L);
        Course course1 = CourseFixture.createCourseWithId(1L);
        Course course2 = CourseFixture.createCourseWithId(2L);

        given(queryDslCourseLikeRepository.findCourseIdsByMemberId(1L)).willReturn(courseIds);
        given(courseRepository.findAllById(courseIds)).willReturn(List.of(course1, course2));

        // when
        ApiResponse result = courseLikeService.getLikedCourses(1L);

        // then
        assertThat(result).isNotNull();
    }
}
