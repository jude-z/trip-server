package core.infra.querydsl.course;

import com.querydsl.jpa.impl.JPAQueryFactory;
import core.domain.entity.coursespot.CourseSpot;
import core.domain.entity.coursespot.QCourseSpot;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class QueryDslCourseSpotRepository {

    private final JPAQueryFactory queryFactory;

    public List<CourseSpot> findByCourseId(Long courseId) {
        QCourseSpot courseSpot = QCourseSpot.courseSpot;
        return queryFactory
                .selectFrom(courseSpot)
                .where(courseSpot.course.courseId.eq(courseId))
                .orderBy(courseSpot.orderInCourse.asc())
                .fetch();
    }
}
