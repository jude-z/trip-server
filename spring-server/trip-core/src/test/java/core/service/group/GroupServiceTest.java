package core.service.group;

import core.api.request.group.AddGroupRequest;
import core.api.response.ApiResponse;
import core.common.Status;
import core.common.exception.CommonException;
import core.domain.entity.destination.Destination;
import core.domain.entity.enroll.Enroll;
import core.domain.entity.group.Group;
import core.domain.entity.group.GroupLike;
import core.domain.entity.member.Member;
import core.fixture.GroupFixture;
import core.fixture.MemberFixture;
import core.infra.jpa.destination.DestinationRepository;
import core.infra.jpa.enroll.EnrollRepository;
import core.infra.jpa.group.GroupLikeRepository;
import core.infra.jpa.group.GroupRepository;
import core.infra.jpa.member.MemberRepository;
import core.infra.projection.group.GroupElement;
import core.infra.querydsl.enroll.QueryDslEnrollRepository;
import core.infra.querydsl.group.QueryDslGroupLikeRepository;
import core.infra.querydsl.group.QueryDslGroupRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class GroupServiceTest {

    @Mock
    private GroupRepository groupRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private EnrollRepository enrollRepository;

    @Mock
    private DestinationRepository destinationRepository;

    @Mock
    private GroupLikeRepository groupLikeRepository;

    @Mock
    private QueryDslGroupRepository queryDslGroupRepository;

    @Mock
    private QueryDslGroupLikeRepository queryDslGroupLikeRepository;

    @Mock
    private QueryDslEnrollRepository queryDslEnrollRepository;

    @InjectMocks
    private GroupService groupService;

    @Test
    @DisplayName("addGroup - success")
    void addGroup_success() {
        // given
        Member member = MemberFixture.create();
        Destination destination = GroupFixture.createDestination();

        AddGroupRequest request = new AddGroupRequest();
        request.setTitle("Trip Group");
        request.setDescription("Let's travel together");
        request.setMaxCount(10);

        given(memberRepository.findById(1L)).willReturn(Optional.of(member));
        given(destinationRepository.findById(1L)).willReturn(Optional.of(destination));
        given(groupRepository.save(any(Group.class))).willAnswer(inv -> inv.getArgument(0));
        given(enrollRepository.save(any(Enroll.class))).willAnswer(inv -> inv.getArgument(0));

        // when
        groupService.addGroup(request, 1L, 1L);

        // then
        verify(groupRepository).save(any(Group.class));
        verify(enrollRepository).save(any(Enroll.class));
    }

    @Test
    @DisplayName("participateGroup - success")
    void participateGroup_success() {
        // given
        Member member = MemberFixture.create();
        Destination destination = GroupFixture.createDestination();
        Group group = GroupFixture.createGroup(member, destination);

        given(memberRepository.findById(1L)).willReturn(Optional.of(member));
        given(groupRepository.findById(1L)).willReturn(Optional.of(group));
        given(enrollRepository.findByMemberAndGroup(member, group)).willReturn(Optional.empty());

        // when
        groupService.participateGroup(1L, 1L);

        // then
        verify(enrollRepository).save(any(Enroll.class));
    }

    @Test
    @DisplayName("participateGroup - already participated throws CommonException")
    void participateGroup_alreadyParticipate() {
        // given
        Member member = MemberFixture.create();
        Destination destination = GroupFixture.createDestination();
        Group group = GroupFixture.createGroup(member, destination);
        Enroll enroll = mock(Enroll.class);

        given(memberRepository.findById(1L)).willReturn(Optional.of(member));
        given(groupRepository.findById(1L)).willReturn(Optional.of(group));
        given(enrollRepository.findByMemberAndGroup(member, group)).willReturn(Optional.of(enroll));

        // when & then
        assertThatThrownBy(() -> groupService.participateGroup(1L, 1L))
                .isInstanceOf(CommonException.class)
                .satisfies(ex -> assertThat(((CommonException) ex).getStatus()).isEqualTo(Status.ALREADY_PARTICIPATE_GROUP));
    }

    @Test
    @DisplayName("leaveGroup - success")
    void leaveGroup_success() {
        // given
        Member member = MemberFixture.create();
        Destination destination = GroupFixture.createDestination();
        Group group = GroupFixture.createGroup(member, destination);
        Enroll enroll = mock(Enroll.class);

        given(memberRepository.findById(1L)).willReturn(Optional.of(member));
        given(groupRepository.findById(1L)).willReturn(Optional.of(group));
        given(queryDslEnrollRepository.findByMemberAndGroupAndAccepted(member, group)).willReturn(Optional.of(enroll));
        given(enroll.isAccepted()).willReturn(true);

        // when
        groupService.leaveGroup(1L, 1L);

        // then
        verify(enrollRepository).delete(enroll);
    }

    @Test
    @DisplayName("leaveGroup - not participated throws CommonException")
    void leaveGroup_notParticipate() {
        // given
        Member member = MemberFixture.create();
        Destination destination = GroupFixture.createDestination();
        Group group = GroupFixture.createGroup(member, destination);

        given(memberRepository.findById(1L)).willReturn(Optional.of(member));
        given(groupRepository.findById(1L)).willReturn(Optional.of(group));
        given(queryDslEnrollRepository.findByMemberAndGroupAndAccepted(member, group)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> groupService.leaveGroup(1L, 1L))
                .isInstanceOf(CommonException.class)
                .satisfies(ex -> assertThat(((CommonException) ex).getStatus()).isEqualTo(Status.NOT_PARTICIPATE_GROUP));
    }

    @Test
    @DisplayName("permitGroup - success")
    void permitGroup_success() {
        // given
        Member member = MemberFixture.create();
        Destination destination = GroupFixture.createDestination();
        Group group = GroupFixture.createGroup(member, destination);
        Enroll enroll = mock(Enroll.class);

        given(memberRepository.findById(1L)).willReturn(Optional.of(member));
        given(groupRepository.findById(1L)).willReturn(Optional.of(group));
        given(enrollRepository.findById(1L)).willReturn(Optional.of(enroll));
        given(enroll.isAccepted()).willReturn(false);

        // when
        groupService.permitGroup(1L, 1L, 1L);

        // then
        verify(enroll).changeAccepted(true);
    }

    @Test
    @DisplayName("permitGroup - not authorized throws CommonException")
    void permitGroup_notAuthorized() {
        // given
        Member member = MemberFixture.create();
        Member otherMember = MemberFixture.createWithId(2L);
        Destination destination = GroupFixture.createDestination();
        Group group = GroupFixture.createGroup(otherMember, destination);

        given(memberRepository.findById(1L)).willReturn(Optional.of(member));
        given(groupRepository.findById(1L)).willReturn(Optional.of(group));

        // when & then
        assertThatThrownBy(() -> groupService.permitGroup(1L, 1L, 1L))
                .isInstanceOf(CommonException.class)
                .satisfies(ex -> assertThat(((CommonException) ex).getStatus()).isEqualTo(Status.NOT_AUTHORIZED));
    }

    @Test
    @DisplayName("permitGroup - already accepted throws CommonException")
    void permitGroup_alreadyAccepted() {
        // given
        Member member = MemberFixture.create();
        Destination destination = GroupFixture.createDestination();
        Group group = GroupFixture.createGroup(member, destination);
        Enroll enroll = mock(Enroll.class);

        given(memberRepository.findById(1L)).willReturn(Optional.of(member));
        given(groupRepository.findById(1L)).willReturn(Optional.of(group));
        given(enrollRepository.findById(1L)).willReturn(Optional.of(enroll));
        given(enroll.isAccepted()).willReturn(true);

        // when & then
        assertThatThrownBy(() -> groupService.permitGroup(1L, 1L, 1L))
                .isInstanceOf(CommonException.class)
                .satisfies(ex -> assertThat(((CommonException) ex).getStatus()).isEqualTo(Status.ALREADY_PARTICIPATE_GROUP));
    }

    @Test
    @DisplayName("deleteGroup - success")
    void deleteGroup_success() {
        // given
        Member member = MemberFixture.create();
        Destination destination = GroupFixture.createDestination();
        Group group = GroupFixture.createGroup(member, destination);

        given(memberRepository.findById(1L)).willReturn(Optional.of(member));
        given(groupRepository.findById(1L)).willReturn(Optional.of(group));
        given(enrollRepository.findByGroup(group)).willReturn(List.of());

        // when
        groupService.deleteGroup(1L, 1L);

        // then
        verify(enrollRepository).deleteAll(any());
    }

    @Test
    @DisplayName("deleteGroup - not authorized throws CommonException")
    void deleteGroup_notAuthorized() {
        // given
        Member member = MemberFixture.create();
        Member otherMember = MemberFixture.createWithId(2L);
        Destination destination = GroupFixture.createDestination();
        Group group = GroupFixture.createGroup(otherMember, destination);

        given(memberRepository.findById(1L)).willReturn(Optional.of(member));
        given(groupRepository.findById(1L)).willReturn(Optional.of(group));

        // when & then
        assertThatThrownBy(() -> groupService.deleteGroup(1L, 1L))
                .isInstanceOf(CommonException.class)
                .satisfies(ex -> assertThat(((CommonException) ex).getStatus()).isEqualTo(Status.NOT_AUTHORIZED));
    }

    @Test
    @DisplayName("groupLike - success")
    void groupLike_success() {
        // given
        Member member = MemberFixture.create();
        Destination destination = GroupFixture.createDestination();
        Group group = GroupFixture.createGroup(member, destination);

        given(memberRepository.findById(1L)).willReturn(Optional.of(member));
        given(groupRepository.findById(1L)).willReturn(Optional.of(group));
        given(queryDslGroupLikeRepository.findByGroupAndMember(group, member)).willReturn(Optional.empty());

        // when
        groupService.groupLike(1L, 1L);

        // then
        verify(groupLikeRepository).save(any(GroupLike.class));
    }

    @Test
    @DisplayName("groupLike - already liked throws CommonException")
    void groupLike_alreadyLiked() {
        // given
        Member member = MemberFixture.create();
        Destination destination = GroupFixture.createDestination();
        Group group = GroupFixture.createGroup(member, destination);
        GroupLike groupLike = mock(GroupLike.class);

        given(memberRepository.findById(1L)).willReturn(Optional.of(member));
        given(groupRepository.findById(1L)).willReturn(Optional.of(group));
        given(queryDslGroupLikeRepository.findByGroupAndMember(group, member)).willReturn(Optional.of(groupLike));

        // when & then
        assertThatThrownBy(() -> groupService.groupLike(1L, 1L))
                .isInstanceOf(CommonException.class)
                .satisfies(ex -> assertThat(((CommonException) ex).getStatus()).isEqualTo(Status.ALREADY_GROUP_LIKE));
    }

    @Test
    @DisplayName("deleteGroupLike - success")
    void deleteGroupLike_success() {
        // given
        Member member = MemberFixture.create();
        Destination destination = GroupFixture.createDestination();
        Group group = GroupFixture.createGroup(member, destination);
        GroupLike groupLike = mock(GroupLike.class);

        given(memberRepository.findById(1L)).willReturn(Optional.of(member));
        given(groupRepository.findById(1L)).willReturn(Optional.of(group));
        given(queryDslGroupLikeRepository.findByGroupAndMember(group, member)).willReturn(Optional.of(groupLike));

        // when
        groupService.deleteGroupLike(1L, 1L);

        // then
        verify(groupLikeRepository).save(groupLike);
    }

    @Test
    @DisplayName("deleteGroupLike - not found throws CommonException")
    void deleteGroupLike_notFound() {
        // given
        Member member = MemberFixture.create();
        Destination destination = GroupFixture.createDestination();
        Group group = GroupFixture.createGroup(member, destination);

        given(memberRepository.findById(1L)).willReturn(Optional.of(member));
        given(groupRepository.findById(1L)).willReturn(Optional.of(group));
        given(queryDslGroupLikeRepository.findByGroupAndMember(group, member)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> groupService.deleteGroupLike(1L, 1L))
                .isInstanceOf(CommonException.class)
                .satisfies(ex -> assertThat(((CommonException) ex).getStatus()).isEqualTo(Status.NOT_FOUND_GROUP_LIKE));
    }

    @Test
    @DisplayName("groups - success with pagination")
    void groups_success() {
        // given
        Member member = MemberFixture.create();
        Page<GroupElement> page = new PageImpl<>(List.of());

        given(memberRepository.findById(1L)).willReturn(Optional.of(member));
        given(queryDslGroupRepository.groups(any(PageRequest.class))).willReturn(page);

        // when
        ApiResponse result = groupService.groups(1, 10, 1L);

        // then
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("groupDetail - success")
    void groupDetail_success() {
        // given
        Member member = MemberFixture.create();
        GroupElement groupElement = mock(GroupElement.class);

        given(memberRepository.findById(1L)).willReturn(Optional.of(member));
        given(queryDslGroupRepository.fetchGroup(1L)).willReturn(Optional.of(groupElement));

        // when
        ApiResponse result = groupService.groupDetail(1L, 1L);

        // then
        assertThat(result).isNotNull();
    }
}
