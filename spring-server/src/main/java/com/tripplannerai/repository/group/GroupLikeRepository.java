package com.tripplannerai.repository.group;

import com.tripplannerai.entity.group.Group;
import com.tripplannerai.entity.group.GroupLike;
import com.tripplannerai.entity.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface GroupLikeRepository extends JpaRepository<GroupLike, Long> {

    @Query("select gr from GroupLike gr left join gr.group g left join gr.member m where g = :group and m = :member")
    Optional<GroupLike> findByGroupAndMember(Group group, Member member);
}
