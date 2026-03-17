package core.infra.jpa.kindplace;

import core.domain.entity.kindplace.KindPlace;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KindPlaceRepository extends JpaRepository<KindPlace, Long> {
}
