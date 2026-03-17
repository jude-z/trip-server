package core.infra.querydsl.course;

import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import core.domain.entity.course.Course;
import core.domain.entity.course.QCourse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class QueryDslCourseRepository {

    private final JPAQueryFactory queryFactory;

    public List<Course> findRandomCourses() {
        QCourse course = QCourse.course;
        return queryFactory
                .selectFrom(course)
                .orderBy(Expressions.numberTemplate(Double.class, "function('random')").asc())
                .limit(8)
                .fetch();
    }
}
