package core.infra.jpa.searchlog;

import core.domain.entity.searchlog.SearchLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SearchLogRepository extends JpaRepository<SearchLog, Long> {
}
