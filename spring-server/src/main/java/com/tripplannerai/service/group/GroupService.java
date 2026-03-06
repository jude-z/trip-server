package com.tripplannerai.service.group;

import com.tripplannerai.common.exception.destination.NotFoundDDestinationException;
import com.tripplannerai.common.exception.group.*;
import com.tripplannerai.dto.request.group.AddGroupRequest;
import com.tripplannerai.dto.response.group.*;
import com.tripplannerai.entity.destination.Destination;
import com.tripplannerai.entity.enroll.Enroll;
import com.tripplannerai.entity.group.Group;
import com.tripplannerai.entity.group.GroupLike;
import com.tripplannerai.entity.member.Member;
import com.tripplannerai.common.exception.enroll.NotFoundEnrollException;
import com.tripplannerai.common.exception.member.NotAuthorizeException;
import com.tripplannerai.common.exception.member.NotFoundMemberException;
import com.tripplannerai.mapper.EnrollFactory;
import com.tripplannerai.mapper.GroupFactory;
import com.tripplannerai.repository.destination.DestinationRepository;
import com.tripplannerai.repository.enroll.EnrollRepository;
import com.tripplannerai.repository.group.GroupLikeRepository;
import com.tripplannerai.repository.group.GroupRepository;
import com.tripplannerai.repository.member.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.tripplannerai.util.ConstClass.*;

@Service
@Transactional
@RequiredArgsConstructor
public class GroupService {
    private final GroupRepository groupRepository;
    private final MemberRepository memberRepository;
    private final EnrollRepository enrollRepository;
    private final DestinationRepository destinationRepository;
    private final GroupLikeRepository groupLikeRepository;
    public AddGroupResponse addGroup(AddGroupRequest addGroupRequest, Long id,Long destinationId) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new NotFoundMemberException("not found Member!!"));
        Destination destination = destinationRepository.findById(destinationId)
                .orElseThrow(() -> new NotFoundDDestinationException("not found Destination!!"));
        Group group = GroupFactory.from(addGroupRequest,member,destination);
        Enroll enroll = EnrollFactory.from(member, group, true);
        groupRepository.save(group);
        enrollRepository.save(enroll);
        return AddGroupResponse.of(SUCCESS_CODE,SUCCESS_MESSAGE,group);
    }

    public ParticipateGroupResponse participateGroup(Long groupId, Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new NotFoundMemberException("not found Member!!"));
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new NotFoundGroupException("not found group"));
        Optional<Enroll> optionalEnroll = enrollRepository.findByMemberAndGroup(member, group);
        if(optionalEnroll.isPresent()) {
            throw new AlreadyParticipateException("already participate");
        }
        Enroll enroll = EnrollFactory.from(member, group, false);
        enrollRepository.save(enroll);
        group.plusParticipateCount();

        return ParticipateGroupResponse.of(SUCCESS_CODE,SUCCESS_MESSAGE);
    }

    public LeaveGroupResponse leaveGroup(Long groupId, Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new NotFoundMemberException("not found Member!!"));
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new NotFoundGroupException("not found group"));
        Enroll enroll = enrollRepository.findByMemberAndGroupAndAccepted(member, group)
                .orElseThrow(() -> new NotParticipateException("member didn't participate group"));
        if(enroll.isAccepted()){
            group.minusCount();
        }else{
            group.minusParticipateCount();
        }
        enrollRepository.delete(enroll);
        return LeaveGroupResponse.of(SUCCESS_CODE,SUCCESS_MESSAGE);
    }


    public ParticipateGroupResponse permitGroup(Long groupId, Long id, Long enrollId) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new NotFoundMemberException("not found Member!!"));
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new NotFoundGroupException("not found Group!!"));
        Long memberId = member.getId();
        boolean authorize = group.getMember().getId().equals(memberId);

        if(!authorize) throw new NotAuthorizeException("not authorized");
        Enroll enroll = enrollRepository.findById(enrollId).orElseThrow(() -> new NotFoundEnrollException("not found Enroll!!"));
        boolean accepted = enroll.isAccepted();
        if(accepted) throw new AlreadyParticipateException("already participate");
        enroll.changeAccepted(true);
        group.plusCount();
        return ParticipateGroupResponse.of(SUCCESS_CODE,SUCCESS_MESSAGE);
    }

    public ApplyGroupResponse applyGroups(Long groupId, Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new NotFoundMemberException("not found Member!!"));
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new NotFoundGroupException("not found Group!!"));
        Long memberId = member.getId();
        boolean authorize = group.getMember().getId().equals(memberId);

        if(!authorize) throw new NotAuthorizeException("not authorized");
        List<ApplyElement> applyElements = enrollRepository.findByGroupAndApply(group);
        return ApplyGroupResponse.of(SUCCESS_CODE,SUCCESS_MESSAGE,applyElements);
    }


    public ApplyGroupResponse participateGroups(Long groupId, Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new NotFoundMemberException("not found Member!!"));
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new NotFoundGroupException("not found Group!!"));
        Long memberId = member.getId();
        boolean authorize = group.getMember().getId().equals(memberId);

        if(!authorize) throw new NotAuthorizeException("not authorized");
        List<ApplyElement> applyElements = enrollRepository.findByGroupAndParticipate(group);
        return ApplyGroupResponse.of(SUCCESS_CODE,SUCCESS_MESSAGE,applyElements);
    }

    public DeleteGroupResponse deleteGroup(Long groupId, Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new NotFoundMemberException("not found Member!!"));
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new NotFoundGroupException("not found Group!!"));
        Long memberId = member.getId();
        boolean authorize = group.getMember().getId().equals(memberId);
        if(!authorize) throw new NotAuthorizeException("not authorized");
        List<Enroll> enrolls = enrollRepository.findByGroup(group);
        enrollRepository.deleteAll(enrolls);
        group.changeStatus(false);
        return DeleteGroupResponse.of(SUCCESS_CODE,SUCCESS_MESSAGE);
    }

    public GroupLikeResponse groupLike(Long groupId, Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new NotFoundMemberException("not found Member!!"));
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new NotFoundGroupException("not found Group!!"));
        Optional<GroupLike> optionalGroupLike = groupLikeRepository.findByGroupAndMember(group, member);
        if(optionalGroupLike.isPresent()) throw new AlreadyGroupLikeException("Already GroupLike");
        GroupLike groupLike = GroupLike.of(member, group);
        groupLikeRepository.save(groupLike);
        group.plusGroupLikeCount();
        return GroupLikeResponse.of(SUCCESS_CODE,SUCCESS_MESSAGE);

    }

    public GroupLikeResponse deleteGroupLike(Long groupId, Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new NotFoundMemberException("not found Member!!"));
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new NotFoundGroupException("not found Group!!"));
        GroupLike groupLike = groupLikeRepository.findByGroupAndMember(group, member)
                .orElseThrow(() -> new NotFoundGroupLikeException("Not Found GroupLike!!"));
        groupLikeRepository.save(groupLike);
        group.minusGroupLikeCount();
        return GroupLikeResponse.of(SUCCESS_CODE,SUCCESS_MESSAGE);
    }

    public GroupsResponse groups(Integer pageNum, Integer pageSize, Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new NotFoundMemberException("not found Member!!"));
        PageRequest pageRequest = PageRequest.of(pageNum - 1, pageSize,Sort.by(Sort.Direction.ASC,"groupId"));
        Page<GroupElement> page = groupRepository.groups(pageRequest);
        List<GroupElement> content = page.getContent();
        boolean hasNext = page.hasNext();
        return GroupsResponse.of(SUCCESS_CODE,SUCCESS_MESSAGE,content,hasNext);
    }

    public GroupResponse groupDetail(Long groupId, Long id) {
        memberRepository.findById(id)
                .orElseThrow(() -> new NotFoundMemberException("not found Member!!"));
        GroupElement groupElement = groupRepository.fetchGroup(groupId)
                .orElseThrow(() -> new NotFoundGroupException("not found Group!!"));
        return GroupResponse.of(SUCCESS_CODE,SUCCESS_MESSAGE,groupElement);

    }
}
