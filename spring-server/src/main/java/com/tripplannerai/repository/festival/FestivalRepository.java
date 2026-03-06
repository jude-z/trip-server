package com.tripplannerai.repository.festival;

import com.tripplannerai.entity.festival.Festival;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FestivalRepository extends JpaRepository<Festival,Long> {
    Optional<Festival> findByUserIdAndContentId(Long userId, String contentId);
    List<Festival> findAllByUserId(Long userId);
    Long countByContentId(String contentId);
    boolean existsByUserIdAndContentId(Long userId, String contentId);
}
