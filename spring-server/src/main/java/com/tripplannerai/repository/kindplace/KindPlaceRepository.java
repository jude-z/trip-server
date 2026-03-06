package com.tripplannerai.repository.kindplace;

import com.tripplannerai.entity.kindplace.KindPlace;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface KindPlaceRepository extends JpaRepository<KindPlace, Long> {
    @Query("select k from KindPlace k where k.address like concat('%',:address, '%')")
    List<KindPlace> fetchKindPlaces(String address, Pageable pageable);
}
