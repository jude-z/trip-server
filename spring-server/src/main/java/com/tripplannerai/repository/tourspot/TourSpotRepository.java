package com.tripplannerai.repository.tourspot;

import com.tripplannerai.entity.tourspot.TourSpot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TourSpotRepository extends JpaRepository<TourSpot,Long> {
    @Query("SELECT ts FROM TourSpot ts " +
            "JOIN FETCH ts.destination d " +
            "JOIN FETCH d.category " +
            "WHERE ts.itinerary.itineraryId = :itineraryId " +
            "ORDER BY ts.order")
    List<TourSpot> findAllByItineraryIdWithDetails(@Param("itineraryId")Long itineraryId);
}
