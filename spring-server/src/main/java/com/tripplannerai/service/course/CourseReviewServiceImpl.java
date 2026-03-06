package com.tripplannerai.service.course;


import com.tripplannerai.common.exception.course.AlreadyExistsReviewException;
import com.tripplannerai.common.exception.course.NotFoundCourseException;
import com.tripplannerai.common.exception.member.NotFoundMemberException;
import com.tripplannerai.dto.request.course.CourseReviewRequest;
import com.tripplannerai.entity.course.Course;
import com.tripplannerai.entity.coursereview.CourseReview;
import com.tripplannerai.entity.member.Member;
import com.tripplannerai.repository.course.CourseRepository;
import com.tripplannerai.repository.course.CourseReviewRepository;
import com.tripplannerai.repository.member.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CourseReviewServiceImpl implements CourseReviewService{

    private final CourseRepository courseRepository;
    private final MemberRepository memberRepository;
    private final CourseReviewRepository courseReviewRepository;
    private final RedisTemplate<String,String> redisTemplate;

    @Transactional
    @Override
    public void addReview(CourseReviewRequest request, Long memberId) {
        Long courseId = request.getCourseId();
        long rating = request.getRating();
        Course course = courseRepository.findById(courseId).orElseThrow(()->new NotFoundCourseException("Not found course"));
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new NotFoundMemberException("Not found member"));
        if (courseReviewRepository.existsByCourseAndMember(course, member)) {
            throw new AlreadyExistsReviewException("이미 회원이 리뷰를 작성한 코스입니다.");
        }
        CourseReview courseReview = CourseReview.builder()
                .member(member)
                .course(course)
                .rating((int)rating)
                .review(request.getReview())
                .build();
        courseReviewRepository.save(courseReview);

        String ratingSumKey = "course:"+courseId+":rating_sum";
        String reviewCountKey = "course:"+courseId+":review_count";

        redisTemplate.opsForValue().increment(ratingSumKey,rating);
        redisTemplate.opsForValue().increment(reviewCountKey,1);

        Long ratingSum = Long.parseLong(redisTemplate.opsForValue().get(ratingSumKey));
        Integer count = Integer.parseInt(redisTemplate.opsForValue().get(reviewCountKey));
        double average = ratingSum.doubleValue()/count;
        redisTemplate.opsForValue().set("course:"+courseId+":rating_avg",String.valueOf(average));

    }

    @Transactional
    @Scheduled(cron = "0 */10 * * * *") //10분마다
    public void syncRatingToDB() {
        Set<Long> courseIds = getCourseIdsFromRedis();
        for(Long courseId : courseIds) {
            String reviewCountKey = "course:" + courseId + ":review_count";
            String ratingAvgKey = "course:" + courseId + ":rating_avg";
            String ratingAvg = redisTemplate.opsForValue().get(ratingAvgKey);
            String reviewCount = redisTemplate.opsForValue().get(reviewCountKey);

            if (ratingAvg == null || reviewCount == null) continue; // null 방지

            Double avg = Double.parseDouble(ratingAvg);
            Integer count = Integer.parseInt(reviewCount);
            if (count == 0) continue; // 0으로 나누는 것 방지

            double avgRating = Math.round(avg*10)/10.0;

            courseRepository.updateRating(courseId, avgRating, count);
        }

    }
    public Set<Long> getCourseIdsFromRedis() {
        Set<String> keys = redisTemplate.keys("course:*:rating_sum");
        return Optional.ofNullable(keys)
                .orElse(Collections.emptySet())
                .stream()
                .map(key -> {
                    try {
                        String[] parts = key.split(":");
                        return Long.parseLong(parts[1]);
                    } catch (Exception e) {
                        log.warn("Invalid Redis key format: {}", key);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }
}
