package core.domain.mapper;

import core.api.request.group.AddGroupRequest;
import core.domain.entity.destination.Destination;
import core.domain.entity.group.Group;
import core.domain.entity.member.Member;

public class GroupFactory {
    public static Group from(AddGroupRequest addGroupRequest, Member member,Destination destination) {
        return Group.builder()
                .title(addGroupRequest.getTitle())
                .description(addGroupRequest.getDescription())
                .startDate(addGroupRequest.getStartDate())
                .endDate(addGroupRequest.getEndDate())
                .member(member)
                .count(1)
                .maxCount(addGroupRequest.getMaxCount())
                .destination(destination)
                .status(true)
                .build();
    }
}
