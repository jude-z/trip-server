package core.service.group;

import core.api.response.ApiResponse;
import core.api.response.ApiStatusResponse;
import core.api.response.ApiDataResponse;
import core.api.request.group.AddGroupRequest;
import core.common.Status;
import core.common.exception.CommonException;
import core.domain.entity.destination.Destination;
import core.domain.entity.enroll.Enroll;
import core.domain.entity.group.Group;
import core.domain.entity.group.GroupLike;
import core.domain.entity.member.Member;
import core.domain.mapper.EnrollFactory;
import core.domain.mapper.GroupFactory;
import core.infra.jpa.destination.DestinationRepository;
import core.infra.jpa.enroll.EnrollRepository;
import core.infra.jpa.group.GroupLikeRepository;
import core.infra.jpa.group.GroupRepository;
import core.infra.jpa.member.MemberRepository;
import core.infra.projection.group.ApplyElement;
import core.infra.projection.group.GroupElement;
import core.infra.querydsl.enroll.QueryDslEnrollRepository;
import core.infra.querydsl.group.QueryDslGroupLikeRepository;
import core.infra.querydsl.group.QueryDslGroupRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class GroupService {
    private final GroupRepository groupRepository;
    private final MemberRepository memberRepository;
    private final EnrollRepository enrollRepository;
    private final DestinationRepository destinationRepository;
    private final GroupLikeRepository groupLikeRepository;
    private final QueryDslGroupRepository queryDslGroupRepository;
    private final QueryDslGroupLikeRepository queryDslGroupLikeRepository;
    private final QueryDslEnrollRepository queryDslEnrollRepository;
    public ApiResponse addGroup(AddGroupRequest addGroupRequest, Long id, Long destinationId) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new CommonException(Status.NOT_FOUND_MEMBER));
        Destination destination = destinationRepository.findById(destinationId)
                .orElseThrow(() -> new CommonException(Status.NOT_FOUND_DESTINATION));
        Group group = GroupFactory.from(addGroupRequest,member,destination);
        Enroll enroll = EnrollFactory.from(member, group, true);
        groupRepository.save(group);
        enrollRepository.save(enroll);
        return ApiDataResponse.of(group, Status.SUCCESS);
    }

    public ApiResponse participateGroup(Long groupId, Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new CommonException(Status.NOT_FOUND_MEMBER));
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new CommonException(Status.NOT_FOUND_GROUP));
        Optional<Enroll> optionalEnroll = enrollRepository.findByMemberAndGroup(member, group);
        if(optionalEnroll.isPresent()) {
            throw new CommonException(Status.ALREADY_PARTICIPATE_GROUP);
        }
        Enroll enroll = EnrollFactory.from(member, group, false);
        enrollRepository.save(enroll);
        group.plusParticipateCount();

        return ApiStatusResponse.of(Status.SUCCESS);
    }

    public ApiResponse leaveGroup(Long groupId, Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new CommonException(Status.NOT_FOUND_MEMBER));
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new CommonException(Status.NOT_FOUND_GROUP));
        Enroll enroll = queryDslEnrollRepository.findByMemberAndGroupAndAccepted(member, group)
                .orElseThrow(() -> new CommonException(Status.NOT_PARTICIPATE_GROUP));
        if(enroll.isAccepted()){
            group.minusCount();
        }else{
            group.minusParticipateCount();
        }
        enrollRepository.delete(enroll);
        return ApiStatusResponse.of(Status.SUCCESS);
    }


    public ApiResponse permitGroup(Long groupId, Long id, Long enrollId) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new CommonException(Status.NOT_FOUND_MEMBER));
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new CommonException(Status.NOT_FOUND_GROUP));
        Long memberId = member.getId();
        boolean authorize = group.getMember().getId().equals(memberId);

        if(!authorize) throw new CommonException(Status.NOT_AUTHORIZED);
        Enroll enroll = enrollRepository.findById(enrollId).orElseThrow(() -> new CommonException(Status.NOT_FOUND_ENROLL));
        boolean accepted = enroll.isAccepted();
        if(accepted) throw new CommonException(Status.ALREADY_PARTICIPATE_GROUP);
        enroll.changeAccepted(true);
        group.plusCount();
        return ApiStatusResponse.of(Status.SUCCESS);
    }

    public ApiResponse applyGroups(Long groupId, Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new CommonException(Status.NOT_FOUND_MEMBER));
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new CommonException(Status.NOT_FOUND_GROUP));
        Long memberId = member.getId();
        boolean authorize = group.getMember().getId().equals(memberId);

        if(!authorize) throw new CommonException(Status.NOT_AUTHORIZED);
        List<ApplyElement> applyElements = queryDslEnrollRepository.findByGroupAndApply(group);
        return ApiDataResponse.of(applyElements, Status.SUCCESS);
    }


    public ApiResponse participateGroups(Long groupId, Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new CommonException(Status.NOT_FOUND_MEMBER));
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new CommonException(Status.NOT_FOUND_GROUP));
        Long memberId = member.getId();
        boolean authorize = group.getMember().getId().equals(memberId);

        if(!authorize) throw new CommonException(Status.NOT_AUTHORIZED);
        List<ApplyElement> applyElements = queryDslEnrollRepository.findByGroupAndParticipate(group);
        return ApiDataResponse.of(applyElements, Status.SUCCESS);
    }

    public ApiResponse deleteGroup(Long groupId, Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new CommonException(Status.NOT_FOUND_MEMBER));
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new CommonException(Status.NOT_FOUND_GROUP));
        Long memberId = member.getId();
        boolean authorize = group.getMember().getId().equals(memberId);
        if(!authorize) throw new CommonException(Status.NOT_AUTHORIZED);
        List<Enroll> enrolls = enrollRepository.findByGroup(group);
        enrollRepository.deleteAll(enrolls);
        group.changeStatus(false);
        return ApiStatusResponse.of(Status.SUCCESS);
    }

    public ApiResponse groupLike(Long groupId, Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new CommonException(Status.NOT_FOUND_MEMBER));
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new CommonException(Status.NOT_FOUND_GROUP));
        Optional<GroupLike> optionalGroupLike = queryDslGroupLikeRepository.findByGroupAndMember(group, member);
        if(optionalGroupLike.isPresent()) throw new CommonException(Status.ALREADY_GROUP_LIKE);
        GroupLike groupLike = GroupLike.of(member, group);
        groupLikeRepository.save(groupLike);
        group.plusGroupLikeCount();
        return ApiStatusResponse.of(Status.SUCCESS);

    }

    public ApiResponse deleteGroupLike(Long groupId, Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new CommonException(Status.NOT_FOUND_MEMBER));
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new CommonException(Status.NOT_FOUND_GROUP));
        GroupLike groupLike = queryDslGroupLikeRepository.findByGroupAndMember(group, member)
                .orElseThrow(() -> new CommonException(Status.NOT_FOUND_GROUP_LIKE));
        groupLikeRepository.save(groupLike);
        group.minusGroupLikeCount();
        return ApiStatusResponse.of(Status.SUCCESS);
    }

    public ApiResponse groups(Integer pageNum, Integer pageSize, Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new CommonException(Status.NOT_FOUND_MEMBER));
        PageRequest pageRequest = PageRequest.of(pageNum - 1, pageSize,Sort.by(Sort.Direction.ASC,"groupId"));
        Page<GroupElement> page = queryDslGroupRepository.groups(pageRequest);
        List<GroupElement> content = page.getContent();
        boolean hasNext = page.hasNext();
        return ApiDataResponse.of(Map.of("content", content, "hasNext", hasNext), Status.SUCCESS);
    }

    public ApiResponse groupDetail(Long groupId, Long id) {
        memberRepository.findById(id)
                .orElseThrow(() -> new CommonException(Status.NOT_FOUND_MEMBER));
        GroupElement groupElement = queryDslGroupRepository.fetchGroup(groupId)
                .orElseThrow(() -> new CommonException(Status.NOT_FOUND_GROUP));
        return ApiDataResponse.of(groupElement, Status.SUCCESS);

    }
}
