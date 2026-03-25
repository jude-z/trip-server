package com.mockserver.repository;

import com.mockserver.domain.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment,Long> {
    Optional<Payment> findByPaymentKey(String paymentKey);

    @Query("SELECT p FROM Payment p WHERE " +
            "(p.retry = 0 AND p.createTime <= :tenMinAgo) OR " +
            "(p.retry = 1 AND p.createTime <= :thirtyMinAgo) OR " +
            "(p.retry = 2 AND p.createTime <= :sixtyMinAgo)")
    List<Payment> findRetryTargets(LocalDateTime tenMinAgo, LocalDateTime thirtyMinAgo, LocalDateTime sixtyMinAgo);

    Long countByPaymentKey(String paymentKey);
}
