package com.tripplannerai.repository.searchlog;

import com.tripplannerai.entity.searchlog.SearchLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SearchLogRepository extends JpaRepository<SearchLog, Long> {
}
