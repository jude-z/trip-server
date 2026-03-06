package com.tripplannerai.repository.impotency;

import com.tripplannerai.entity.pay.impotency.Impotency;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ImpotencyRepository extends JpaRepository<Impotency, Long> {
    @Lock(LockModeType.PESSIMISTIC_READ)
    @Query("select i from Impotency i where i.impotencyKey = :impotencyKey")
    Optional<Impotency> findByImpotencyKey(String impotencyKey);
}
