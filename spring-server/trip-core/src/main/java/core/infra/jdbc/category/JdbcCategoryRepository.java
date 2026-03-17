package core.infra.jdbc.category;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

@Repository
public class JdbcCategoryRepository {
    private final JdbcTemplate jdbcTemplate;

    public JdbcCategoryRepository(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }
}
