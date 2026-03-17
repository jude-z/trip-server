package core.infra.querydsl.course;

import com.querydsl.jpa.impl.JPAQueryFactory;
import core.domain.entity.courselike.QCourseLike;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class QueryDslCourseLikeRepository {

    private final JPAQueryFactory queryFactory;

    public List<Long> findCourseIdsByMemberId(Long memberId) {
        QCourseLike courseLike = QCourseLike.courseLike;
        return queryFactory
                .select(courseLike.course.courseId)
                .from(courseLike)
                .where(courseLike.member.id.eq(memberId))
                .fetch();
    }
}
