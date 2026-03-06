package com.tripplannerai.repository.enroll;

import com.tripplannerai.dto.response.group.ApplyElement;
import com.tripplannerai.entity.enroll.Enroll;
import com.tripplannerai.entity.group.Group;
import com.tripplannerai.entity.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface EnrollRepository extends JpaRepository<Enroll, Long> {
    @Query("select e from Enroll e left join e.member m left join e.group g where m = :member and g = :group")
    Optional<Enroll> findByMemberAndGroupAndAccepted(Member member, Group group);



    Optional<Enroll> findByMemberAndGroup(Member member, Group group);

    @Query("select new com.tripplannerai.dto.response.group.ApplyElement(e.enrollId,m.nickname,e.accepted) " +
            "from Enroll e " +
            "left join e.group g " +
            "left join e.member m where e.accepted = false")
    List<ApplyElement> findByGroupAndApply(Group group);

    @Query("select new com.tripplannerai.dto.response.group.ApplyElement(e.enrollId,m.nickname,e.accepted) " +
            "from Enroll e " +
            "left join e.group g " +
            "left join e.member m where e.accepted = true")
    List<ApplyElement> findByGroupAndParticipate(Group group);

    List<Enroll> findByGroup(Group group);
}
