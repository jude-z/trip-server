package com.tripplannerai.repository.keep;

import com.tripplannerai.dto.response.keep.KeepElement;
import com.tripplannerai.entity.destination.Destination;
import com.tripplannerai.entity.keep.Keep;
import com.tripplannerai.entity.member.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface KeepRepository extends JpaRepository<Keep, Long> {
    @Query("select new com.tripplannerai.dto.response.keep.KeepElement(d.contentId) " +
            "from Keep k " +
            "left join k.member m " +
            "left join k.destination d " +
            "where m.id = :id")
    Page<KeepElement> fetchKeeps(Long id, Pageable pageable);
    @Query("select new com.tripplannerai.dto.response.keep.KeepElement(d.contentId) " +
            "from Keep k " +
            "left join k.member m " +
            "left join k.destination d " +
            "where m.id = :id and d.contentId = :contentId")
    KeepElement fetchKeep(String contentId, Long id);

    @Query("select k from Keep k left join k.member m left join k.destination d where m.id = :id and d.contentId = :contentId")
    Optional<Keep> findByMemberAndDestination(String contentId, Long id);

}
