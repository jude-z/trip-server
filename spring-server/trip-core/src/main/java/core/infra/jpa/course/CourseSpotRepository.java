package core.infra.jpa.course;

import core.domain.entity.coursespot.CourseSpot;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseSpotRepository extends JpaRepository<CourseSpot,Long> {
}
