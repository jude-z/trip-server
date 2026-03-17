package core.infra.jpa.comment;

import core.domain.entity.comment.GroupComment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupCommentRepository extends JpaRepository<GroupComment, Long> {
}
