package com.tripplannerai.repository.group;

import com.tripplannerai.dto.response.group.GroupElement;
import com.tripplannerai.entity.group.Group;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface GroupRepository extends JpaRepository<Group, Long> {
    @Query("select new com.tripplannerai.dto.response.group.GroupElement" +
            "(g.groupId,g.title,g.description,g.count,g.participateCount,g.groupLikeCount,g.maxCount,g.startDate,g.endDate,m.id) " +
            "from Group g " +
            "left join g.member m " +
            "where g.status = true")
    Page<GroupElement> groups(Pageable pageable);

    @Query("select new com.tripplannerai.dto.response.group.GroupElement" +
            "(g.groupId,g.title,g.description,g.count,g.participateCount,g.groupLikeCount,g.maxCount,g.startDate,g.endDate,m.id)" +
            "from Group g " +
            "left join g.member m " +
            "where g.groupId = :groupId")
    Optional<GroupElement> fetchGroup(Long groupId);
}
