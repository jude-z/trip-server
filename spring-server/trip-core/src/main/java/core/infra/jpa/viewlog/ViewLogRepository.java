package core.infra.jpa.viewlog;

import core.domain.entity.viewlog.ViewLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ViewLogRepository extends JpaRepository<ViewLog,Long> {
}
