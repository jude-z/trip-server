package com.tripplannerai.repository.itinerary;

import com.tripplannerai.entity.itinerary.Itinerary;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItineraryRepository extends JpaRepository<Itinerary, Long> {
}
