package core.infra.jdbc.comment;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.Collections;
import java.util.List;

@Repository
public class JdbcCommentRepository {

    private final JdbcTemplate jdbcTemplate;

    public JdbcCommentRepository(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void deleteAllById(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }
        String placeholders = String.join(",", Collections.nCopies(ids.size(), "?"));
        jdbcTemplate.update(
                "UPDATE comment SET is_deleted = true WHERE comment_id IN (" + placeholders + ")",
                ids.toArray());
    }
}
