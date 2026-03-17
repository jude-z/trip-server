package core.infra.jpa.fail;

import core.domain.entity.fail.Fail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FailRepository extends JpaRepository<Fail, Long> {
}
