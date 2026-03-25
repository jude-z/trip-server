package core.infra.jpa.point;

import core.domain.entity.member.Member;
import core.domain.entity.point.Point;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface PointRepository extends JpaRepository<Point,Long> {

    @Lock(LockModeType.PESSIMISTIC_READ)
    Optional<Point> findByMember(Member member);

    Optional<Point> findByMemberId(Long memberId);

    @Modifying
    @Transactional
    @Query("UPDATE Point p SET p.amount = 0 WHERE p.member.id = :memberId")
    void resetPointByMemberId(Long memberId);
}
