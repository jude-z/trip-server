package core.infra.jpa.group;

import core.domain.entity.group.GroupLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupLikeRepository extends JpaRepository<GroupLike, Long> {
}
