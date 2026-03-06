package com.tripplannerai.service.course;

import com.tripplannerai.common.exception.course.NotFoundCourseException;
import com.tripplannerai.common.exception.course.NotFoundCourseLikeException;
import com.tripplannerai.common.exception.member.NotFoundMemberException;
import com.tripplannerai.dto.response.course.CourseInfoResponse;
import com.tripplannerai.entity.course.Course;
import com.tripplannerai.entity.courselike.CourseLike;
import com.tripplannerai.entity.member.Member;
import com.tripplannerai.repository.course.CourseLikeRepository;
import com.tripplannerai.repository.course.CourseRepository;
import com.tripplannerai.repository.member.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseLikeServiceImpl implements CourseLikeService{
    private final MemberRepository memberRepository;
    private final CourseRepository courseRepository;
    private final CourseLikeRepository courseLikeRepository;
    @Override
    @Transactional
    public void addLikeCourse(Long courseId, Long userId) {
        Course course = courseRepository.findById(courseId).orElseThrow(() -> new NotFoundCourseException("Course not found"));
        Member member = memberRepository.findById(userId).orElseThrow(() -> new NotFoundMemberException("User not found"));
        boolean alreadyLiked = courseLikeRepository.existsByCourseAndMember(course,member);
        if (alreadyLiked) {
            throw new IllegalStateException("이미 좋아요를 누른 코스입니다.");
        }
        CourseLike courseLike = CourseLike.builder()
                .course(course)
                .member(member)
                .build();
        courseLikeRepository.save(courseLike);
        courseRepository.updateLikeCount(1,courseId);
    }

    @Override
    @Transactional
    public void removeLikeCourse(Long courseId, Long userId) {
        Course course = courseRepository.findById(courseId).orElseThrow(() -> new NotFoundCourseException("Course not found"));
        Member member = memberRepository.findById(userId).orElseThrow(() -> new NotFoundMemberException("User not found"));
        CourseLike courseLike = courseLikeRepository.findByCourseAndMember(course, member).orElseThrow(()-> new NotFoundCourseLikeException("Like not found"));
        courseLikeRepository.delete(courseLike);
        courseRepository.updateLikeCount(-1,courseId);
    }

    @Override
    public boolean isLiked(Long courseId, Long userId) {
        Course course = courseRepository.findById(courseId).orElseThrow(() -> new NotFoundCourseException("Course not found"));
        Member member = memberRepository.findById(userId).orElseThrow(() -> new NotFoundMemberException("User not found"));
        return courseLikeRepository.existsByCourseAndMember(course,member);
    }

    @Override
    public List<CourseInfoResponse> getLikedCourses(Long memberId) {
        List<Long> courseIds = courseLikeRepository.findCourseIdsByMemberId(memberId);
        List<Course> courseList = courseRepository.findAllById(courseIds);
        List<CourseInfoResponse> courseInfoResponseList = courseList.stream()
                .map(CourseInfoResponse::fromEntity)
                .toList();
        return courseInfoResponseList;

    }


}
