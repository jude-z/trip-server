package com.tripplannerai.repository.member;

import com.tripplannerai.entity.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    @Query("select m from Member m left join fetch m.image i where m.email =:email")
    Optional<Member> findByEmail(String email);
    @Query("select m from Member m left join fetch m.image i where m.id = :memberId")
    Optional<Member> fetchById(Long memberId);
    @Query("update Member m set m.ticket = m.ticket + :val where m.id = :userId")
    @Modifying
    void updateTicket(Integer val, Long userId);

}
