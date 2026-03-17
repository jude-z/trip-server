package core.infra.querydsl.destination;

import core.infra.projection.destination.DestinationCategoryQuery;
import core.infra.projection.destination.DestinationQuery;
import core.infra.projection.destination.DestinationTotalQuery;
import core.domain.entity.destination.Destination;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class QueryDslDestinationRepository {

    private final EntityManager entityManager;

    @SuppressWarnings("unchecked")
    public List<DestinationQuery> fetchDestinations(int offset, int limit) {
        return entityManager.createNativeQuery(
                        "select d.contentId from destination order by destination_id asc limit :limit offset :offset",
                        DestinationQuery.class)
                .setParameter("limit", limit)
                .setParameter("offset", offset)
                .getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<DestinationCategoryQuery> fetchDestinationByCategory(int offset, int limit, Long categoryId) {
        return entityManager.createNativeQuery(
                        "select ca_middle.category_id as middle_category_id," +
                                "ca_sub.category_id as sub_category_id," +
                                "ca_middle.name AS middle_category_name," +
                                "ca_sub.name as sub_category_name," +
                                "d.destination_id AS destination_id," +
                                "d.name AS destination_name," +
                                "d.addr1 AS destination_adr1," +
                                "d.addr2 AS destination_adr2," +
                                "d.tel AS destination_tel," +
                                "d.content_id AS destination_content_id," +
                                "d.latitude AS destination_latitude," +
                                "d.longitude AS destination_longtitude," +
                                "d.rating AS destination_rating," +
                                "d.thumbnail_image_url AS destination_thumbnail_image_url " +
                                "FROM category ca_middle " +
                                "JOIN category ca_sub " +
                                "ON ca_sub.parent_id = ca_middle.category_id and ca_middle.category_id is not null " +
                                "JOIN destination d " +
                                "ON d.category_id = ca_middle.category_id OR d.category_id = ca_sub.category_id " +
                                "where ca_middle.category_id = :categoryId " +
                                "order by d.destination_id asc limit :limit offset :offset",
                        DestinationCategoryQuery.class)
                .setParameter("categoryId", categoryId)
                .setParameter("limit", limit)
                .setParameter("offset", offset)
                .getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<DestinationTotalQuery> fetchDestinationsByTotal() {
        return entityManager.createNativeQuery(
                        "select * from " +
                                "(select ca_middle.category_id as middle_category_id," +
                                "ca_sub.category_id as sub_category_id," +
                                "ca_middle.name AS middle_category_name," +
                                "ca_sub.name as sub_category_name," +
                                "d.destination_id AS destination_id," +
                                "d.name AS destination_name," +
                                "d.addr1 AS destination_adr1," +
                                "d.addr2 AS destination_adr2," +
                                "d.tel AS destination_tel," +
                                "d.content_id AS destination_content_id," +
                                "d.latitude AS destination_latitude," +
                                "d.longitude AS destination_longtitude," +
                                "d.rating AS destination_rating," +
                                "d.thumbnail_image_url AS destination_thumbnail_image_url," +
                                "ROW_NUMBER() OVER (PARTITION BY ca_middle.category_id ORDER BY d.destination_id) AS row_num " +
                                "FROM category ca_middle " +
                                "JOIN category ca_sub " +
                                "ON ca_sub.parent_id = ca_middle.category_id and ca_middle.category_id is not null " +
                                "JOIN destination d " +
                                "ON d.category_id = ca_middle.category_id OR d.category_id = ca_sub.category_id " +
                                ") subquery " +
                                "WHERE row_num <= 9 " +
                                "ORDER BY middle_category_id, row_num",
                        DestinationTotalQuery.class)
                .getResultList();
    }

    public int fetchDestinationsCount(int limit) {
        return ((Number) entityManager.createNativeQuery(
                        "select count(*) from (select destination_id from destination limit :limit) t")
                .setParameter("limit", limit)
                .getSingleResult()).intValue();
    }

    public int fetchDestinationsCategoryCount(Long categoryId, int limit) {
        return ((Number) entityManager.createNativeQuery(
                        "select count(*) from " +
                                "(select d.destination_id " +
                                "from category ca_middle " +
                                "join category ca_sub " +
                                "on ca_sub.parent_id = ca_middle.category_id and ca_middle.category_id is not null " +
                                "join destination d " +
                                "on d.category_id = ca_middle.category_id or d.category_id = ca_sub.category_id " +
                                "where ca_middle.category_id = :categoryId " +
                                "limit :limit) t")
                .setParameter("categoryId", categoryId)
                .setParameter("limit", limit)
                .getSingleResult()).intValue();
    }

    @SuppressWarnings("unchecked")
    public List<Destination> findAllCourses() {
        return entityManager.createNativeQuery("""
                SELECT *
                FROM (
                    SELECT *, ROW_NUMBER() OVER (PARTITION BY address_id ORDER BY destination_id desc) AS rn
                    FROM destination
                    WHERE category_id>=172
                      AND LENGTH(origin_image_url) > 0
                      AND address_id IS NOT NULL
                ) sub
                WHERE rn <= 1
                """, Destination.class)
                .getResultList();
    }
}
