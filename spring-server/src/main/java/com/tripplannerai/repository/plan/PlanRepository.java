package com.tripplannerai.repository.plan;

import com.tripplannerai.entity.plan.Plan;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PlanRepository extends JpaRepository<Plan,Long> {
    @EntityGraph(attributePaths = {"itineraries"})
    @Query("SELECT p FROM Plan p WHERE p.member.id = :memberId")
    List<Plan> findAllByMember_Id(@Param("memberId") Long memberId);

}
