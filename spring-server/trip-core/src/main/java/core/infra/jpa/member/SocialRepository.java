package core.infra.jpa.member;

import core.domain.entity.social.Social;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SocialRepository extends JpaRepository<Social,Long> {
}
