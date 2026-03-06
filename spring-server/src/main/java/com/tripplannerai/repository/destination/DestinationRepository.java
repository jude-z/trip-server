package com.tripplannerai.repository.destination;

import com.tripplannerai.dto.response.destination.DestinationCategoryQuery;
import com.tripplannerai.dto.response.destination.DestinationQuery;
import com.tripplannerai.dto.response.destination.DestinationTotalQuery;
import com.tripplannerai.entity.destination.Destination;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface DestinationRepository extends JpaRepository<Destination, Long> {

    @Query(value = "select d.contentId from destination order by destination_id asc limit :limit offset :offset"
            ,nativeQuery = true)
    List<DestinationQuery> fetchDestinations(int offset, int limit);

    @Query(value =
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
            "order by d.destination_id asc limit :limit offset :offset"
            ,nativeQuery = true)
    List<DestinationCategoryQuery> fetchDestinationByCategory(int offset, int limit, Long categoryId);

    @Query(value = "select * from " +
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
            "ORDER BY middle_category_id, row_num"
            ,nativeQuery = true)
    List<DestinationTotalQuery> fetchDestinationsByTotal();

    @Query(value = "select count(*) from (select destination_id from destination limit :limit) t"
            ,nativeQuery = true)
    int fetchDestinationsCount(int limit);

    @Query(value = "select count(*) from " +
            "(select d.destination_id " +
            "from category ca_middle " +
            "join category ca_sub " +
            "on ca_sub.parent_id = ca_middle.category_id and ca_middle.category_id is not null " +
            "join destination d " +
            "on d.category_id = ca_middle.category_id or d.category_id = ca_sub.category_id " +
            "where ca_middle.category_id = :categoryId " +
            "limit :limit) t"
            ,nativeQuery = true)
    int fetchDestinationsCategoryCount(Long categoryId,int limit);


    @Query(value = """
    SELECT *
    FROM (
        SELECT *, ROW_NUMBER() OVER (PARTITION BY address_id ORDER BY destination_id desc) AS rn
        FROM destination
        WHERE category_id>=172
          AND LENGTH(origin_image_url) > 0
          AND address_id IS NOT NULL
    ) sub
    WHERE rn <= 1
    """, nativeQuery = true)
    List<Destination> findAllCourses();

    Optional<Destination> findByContentId(String contentId);

    List<Destination> findAllByContentIdIn(List<String> contentIds);
}
