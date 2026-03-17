package core.infra.jpa.keep;

import core.domain.entity.keep.Keep;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KeepRepository extends JpaRepository<Keep, Long> {
}
