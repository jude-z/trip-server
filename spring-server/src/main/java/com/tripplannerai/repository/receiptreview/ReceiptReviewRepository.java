package com.tripplannerai.repository.receiptreview;

import com.tripplannerai.entity.receiptreview.ReceiptReview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ReceiptReviewRepository extends JpaRepository<ReceiptReview,Long> {
    @Query(
            value = "SELECT r FROM ReceiptReview r LEFT JOIN FETCH r.images JOIN FETCH r.member",
            countQuery = "SELECT COUNT(r) FROM ReceiptReview r"
    )
    Page<ReceiptReview> findAllWithImagesAndMember(Pageable pageable);

    @Query("SELECT r FROM ReceiptReview r " +
            "LEFT JOIN FETCH r.images " +
            "JOIN FETCH r.member " +
            "WHERE r.receiptReviewId = :id")
    Optional<ReceiptReview> findByIdWithImagesAndMember(@Param("id") Long id);

}
