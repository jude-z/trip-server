package core.infra.jpa.destination;

import core.domain.entity.destination.Destination;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DestinationRepository extends JpaRepository<Destination, Long> {

    Optional<Destination> findByContentId(String contentId);

    List<Destination> findAllByContentIdIn(List<String> contentIds);
}
