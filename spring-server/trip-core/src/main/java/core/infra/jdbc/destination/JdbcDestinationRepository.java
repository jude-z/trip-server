package core.infra.jdbc.destination;

import core.infra.projection.destination.DestinationCategoryQuery;
import core.infra.projection.destination.DestinationQuery;
import core.infra.projection.destination.DestinationTotalQuery;
import core.domain.entity.destination.Destination;
import jakarta.persistence.EntityManager;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class JdbcDestinationRepository {

    private final JdbcTemplate jdbcTemplate;
    private final EntityManager entityManager;

    public JdbcDestinationRepository(DataSource dataSource, EntityManager entityManager) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.entityManager = entityManager;
    }

    public List<DestinationQuery> fetchDestinations(int offset, int limit) {
        return jdbcTemplate.query(
                "SELECT content_id, destination_id FROM destination ORDER BY destination_id ASC LIMIT ? OFFSET ?",
                destinationQueryRowMapper(),
                limit, offset);
    }

    public List<DestinationCategoryQuery> fetchDestinationByCategory(int offset, int limit, Long categoryId) {
        return jdbcTemplate.query(
                "SELECT ca_middle.category_id AS middle_category_id, " +
                        "ca_sub.category_id AS sub_category_id, " +
                        "ca_middle.name AS middle_category_name, " +
                        "ca_sub.name AS sub_category_name, " +
                        "d.destination_id AS destination_id, " +
                        "d.name AS destination_name, " +
                        "d.addr1 AS destination_adr1, " +
                        "d.addr2 AS destination_adr2, " +
                        "d.tel AS destination_tel, " +
                        "d.content_id AS destination_content_id, " +
                        "d.latitude AS destination_latitude, " +
                        "d.longitude AS destination_longtitude, " +
                        "d.rating AS destination_rating, " +
                        "d.thumbnail_image_url AS destination_thumbnail_image_url " +
                        "FROM category ca_middle " +
                        "JOIN category ca_sub ON ca_sub.parent_id = ca_middle.category_id AND ca_middle.category_id IS NOT NULL " +
                        "JOIN destination d ON d.category_id = ca_middle.category_id OR d.category_id = ca_sub.category_id " +
                        "WHERE ca_middle.category_id = ? " +
                        "ORDER BY d.destination_id ASC LIMIT ? OFFSET ?",
                destinationCategoryQueryRowMapper(),
                categoryId, limit, offset);
    }

    public List<DestinationTotalQuery> fetchDestinationsByTotal() {
        return jdbcTemplate.query(
                "SELECT * FROM " +
                        "(SELECT ca_middle.category_id AS middle_category_id, " +
                        "ca_sub.category_id AS sub_category_id, " +
                        "ca_middle.name AS middle_category_name, " +
                        "ca_sub.name AS sub_category_name, " +
                        "d.destination_id AS destination_id, " +
                        "d.name AS destination_name, " +
                        "d.addr1 AS destination_adr1, " +
                        "d.addr2 AS destination_adr2, " +
                        "d.tel AS destination_tel, " +
                        "d.content_id AS destination_content_id, " +
                        "d.latitude AS destination_latitude, " +
                        "d.longitude AS destination_longtitude, " +
                        "d.rating AS destination_rating, " +
                        "d.thumbnail_image_url AS destination_thumbnail_image_url, " +
                        "ROW_NUMBER() OVER (PARTITION BY ca_middle.category_id ORDER BY d.destination_id) AS row_num " +
                        "FROM category ca_middle " +
                        "JOIN category ca_sub ON ca_sub.parent_id = ca_middle.category_id AND ca_middle.category_id IS NOT NULL " +
                        "JOIN destination d ON d.category_id = ca_middle.category_id OR d.category_id = ca_sub.category_id) subquery " +
                        "WHERE row_num <= 9 ORDER BY middle_category_id, row_num",
                destinationTotalQueryRowMapper());
    }

    public int fetchDestinationsCount(int limit) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT count(*) FROM (SELECT destination_id FROM destination LIMIT ?) t",
                Integer.class, limit);
        return count != null ? count : 0;
    }

    public int fetchDestinationsCategoryCount(Long categoryId, int limit) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT count(*) FROM " +
                        "(SELECT d.destination_id FROM category ca_middle " +
                        "JOIN category ca_sub ON ca_sub.parent_id = ca_middle.category_id AND ca_middle.category_id IS NOT NULL " +
                        "JOIN destination d ON d.category_id = ca_middle.category_id OR d.category_id = ca_sub.category_id " +
                        "WHERE ca_middle.category_id = ? LIMIT ?) t",
                Integer.class, categoryId, limit);
        return count != null ? count : 0;
    }

    @SuppressWarnings("unchecked")
    public List<Destination> findAllCourses() {
        return entityManager.createNativeQuery("""
                SELECT *
                FROM (
                    SELECT *, ROW_NUMBER() OVER (PARTITION BY address_id ORDER BY destination_id DESC) AS rn
                    FROM destination
                    WHERE category_id >= 172
                      AND LENGTH(origin_image_url) > 0
                      AND address_id IS NOT NULL
                ) sub
                WHERE rn <= 1
                """, Destination.class)
                .getResultList();
    }

    private RowMapper<DestinationQuery> destinationQueryRowMapper() {
        return (rs, rowNum) -> {
            String contentId = rs.getString("content_id");
            Long destinationId = rs.getLong("destination_id");
            return new DestinationQuery() {
                @Override public String getContentId() { return contentId; }
                @Override public Long getDestinationId() { return destinationId; }
            };
        };
    }

    private RowMapper<DestinationCategoryQuery> destinationCategoryQueryRowMapper() {
        return (rs, rowNum) -> {
            String middleCategoryId = rs.getString("middle_category_id");
            String subCategoryId = rs.getString("sub_category_id");
            String middleCategoryName = rs.getString("middle_category_name");
            String subCategoryName = rs.getString("sub_category_name");
            String destinationId = rs.getString("destination_id");
            String destinationName = rs.getString("destination_name");
            String destinationAddr1 = rs.getString("destination_adr1");
            String destinationAddr2 = rs.getString("destination_adr2");
            String destinationTel = rs.getString("destination_tel");
            String destinationContentId = rs.getString("destination_content_id");
            String destinationLatitude = rs.getString("destination_latitude");
            String destinationLongitude = rs.getString("destination_longtitude");
            String destinationRating = rs.getString("destination_rating");
            String destinationThumbnailImageUrl = rs.getString("destination_thumbnail_image_url");
            return new DestinationCategoryQuery() {
                @Override public String getMiddleCategoryId() { return middleCategoryId; }
                @Override public String getSubCategoryId() { return subCategoryId; }
                @Override public String getMiddleCategoryName() { return middleCategoryName; }
                @Override public String getSubCategoryName() { return subCategoryName; }
                @Override public String getDestinationId() { return destinationId; }
                @Override public String getDestinationName() { return destinationName; }
                @Override public String getDestinationAddr1() { return destinationAddr1; }
                @Override public String getDestinationAddr2() { return destinationAddr2; }
                @Override public String getDestinationTel() { return destinationTel; }
                @Override public String getDestinationContentId() { return destinationContentId; }
                @Override public String getDestinationLatitude() { return destinationLatitude; }
                @Override public String getDestinationLongitude() { return destinationLongitude; }
                @Override public String getDestinationRating() { return destinationRating; }
                @Override public String getDestinationThumbnailImageUrl() { return destinationThumbnailImageUrl; }
            };
        };
    }

    private RowMapper<DestinationTotalQuery> destinationTotalQueryRowMapper() {
        return (rs, rowNum) -> {
            String middleCategoryId = rs.getString("middle_category_id");
            String subCategoryId = rs.getString("sub_category_id");
            String middleCategoryName = rs.getString("middle_category_name");
            String subCategoryName = rs.getString("sub_category_name");
            String destinationId = rs.getString("destination_id");
            String destinationName = rs.getString("destination_name");
            String destinationAddr1 = rs.getString("destination_adr1");
            String destinationAddr2 = rs.getString("destination_adr2");
            String destinationTel = rs.getString("destination_tel");
            String destinationContentId = rs.getString("destination_content_id");
            String destinationLatitude = rs.getString("destination_latitude");
            String destinationLongitude = rs.getString("destination_longtitude");
            String destinationRating = rs.getString("destination_rating");
            String destinationThumbnailImageUrl = rs.getString("destination_thumbnail_image_url");
            return new DestinationTotalQuery() {
                @Override public String getMiddleCategoryId() { return middleCategoryId; }
                @Override public String getSubCategoryId() { return subCategoryId; }
                @Override public String getMiddleCategoryName() { return middleCategoryName; }
                @Override public String getSubCategoryName() { return subCategoryName; }
                @Override public String getDestinationId() { return destinationId; }
                @Override public String getDestinationName() { return destinationName; }
                @Override public String getDestinationAddr1() { return destinationAddr1; }
                @Override public String getDestinationAddr2() { return destinationAddr2; }
                @Override public String getDestinationTel() { return destinationTel; }
                @Override public String getDestinationContentId() { return destinationContentId; }
                @Override public String getDestinationLatitude() { return destinationLatitude; }
                @Override public String getDestinationLongitude() { return destinationLongitude; }
                @Override public String getDestinationRating() { return destinationRating; }
                @Override public String getDestinationThumbnailImageUrl() { return destinationThumbnailImageUrl; }
            };
        };
    }
}
