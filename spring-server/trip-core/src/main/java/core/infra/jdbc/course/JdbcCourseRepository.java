package core.infra.jdbc.course;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

@Repository
public class JdbcCourseRepository {

    private final JdbcTemplate jdbcTemplate;

    public JdbcCourseRepository(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void updateLikeCount(int val, Long courseId) {
        jdbcTemplate.update(
                "UPDATE course SET like_count = like_count + ? WHERE course_id = ?",
                val, courseId);
    }

    public void updateRating(Long courseId, double avgRating, int count) {
        jdbcTemplate.update(
                "UPDATE course SET rating = ?, review_count = ? WHERE course_id = ?",
                avgRating, count, courseId);
    }
}
