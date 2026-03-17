package core.infra.jpa.itinerary;

import core.domain.entity.itinerary.Itinerary;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItineraryRepository extends JpaRepository<Itinerary, Long> {
}
