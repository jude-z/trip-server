package core.infra.jpa.enroll;

import core.domain.entity.enroll.Enroll;
import core.domain.entity.group.Group;
import core.domain.entity.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EnrollRepository extends JpaRepository<Enroll, Long> {

    Optional<Enroll> findByMemberAndGroup(Member member, Group group);

    List<Enroll> findByGroup(Group group);
}
