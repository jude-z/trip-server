package core.infra.jpa.tourspot;

import core.domain.entity.tourspot.TourSpot;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TourSpotRepository extends JpaRepository<TourSpot,Long> {
}
