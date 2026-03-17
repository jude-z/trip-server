package core.service.keep;

import core.api.response.ApiResponse;
import core.common.Status;
import core.common.exception.CommonException;
import core.domain.entity.destination.Destination;
import core.domain.entity.keep.Keep;
import core.domain.entity.member.Member;
import core.fixture.KeepFixture;
import core.fixture.MemberFixture;
import core.infra.jpa.destination.DestinationRepository;
import core.infra.jpa.keep.KeepRepository;
import core.infra.jpa.member.MemberRepository;
import core.infra.projection.keep.KeepElement;
import core.infra.querydsl.keep.QueryDslKeepRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class KeepServiceTest {

    @InjectMocks
    private KeepService keepService;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private DestinationRepository destinationRepository;

    @Mock
    private KeepRepository keepRepository;

    @Mock
    private QueryDslKeepRepository queryDslKeepRepository;

    @Test
    @DisplayName("createKeep - success")
    void createKeep_success() {
        // given
        Long memberId = 1L;
        String contentId = "content-1";
        Member member = MemberFixture.create();
        Destination destination = KeepFixture.createDestinationWithContentId(contentId);

        given(memberRepository.findById(memberId)).willReturn(Optional.of(member));
        given(destinationRepository.findByContentId(contentId)).willReturn(Optional.of(destination));
        given(keepRepository.save(any(Keep.class))).willAnswer(invocation -> invocation.getArgument(0));

        // when
        keepService.createKeep(contentId, memberId);

        // then
        verify(keepRepository).save(any(Keep.class));
    }

    @Test
    @DisplayName("createKeep - member not found")
    void createKeep_memberNotFound() {
        // given
        given(memberRepository.findById(1L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> keepService.createKeep("content-1", 1L))
                .isInstanceOf(CommonException.class)
                .satisfies(ex -> assertThat(((CommonException) ex).getStatus()).isEqualTo(Status.NOT_FOUND_MEMBER));
    }

    @Test
    @DisplayName("createKeep - destination not found")
    void createKeep_destinationNotFound() {
        // given
        Member member = MemberFixture.create();
        given(memberRepository.findById(1L)).willReturn(Optional.of(member));
        given(destinationRepository.findByContentId("content-1")).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> keepService.createKeep("content-1", 1L))
                .isInstanceOf(CommonException.class)
                .satisfies(ex -> assertThat(((CommonException) ex).getStatus()).isEqualTo(Status.NOT_FOUND_DESTINATION));
    }

    @Test
    @DisplayName("deleteKeep - success")
    void deleteKeep_success() {
        // given
        Long memberId = 1L;
        String contentId = "content-1";
        Member member = MemberFixture.create();
        Destination destination = KeepFixture.createDestinationWithContentId(contentId);
        Keep keep = KeepFixture.createKeep(destination, member);

        given(memberRepository.findById(memberId)).willReturn(Optional.of(member));
        given(destinationRepository.findByContentId(contentId)).willReturn(Optional.of(destination));
        given(queryDslKeepRepository.findByMemberAndDestination(contentId, memberId)).willReturn(Optional.of(keep));

        // when
        keepService.deleteKeep(contentId, memberId);

        // then
        verify(keepRepository).delete(keep);
    }

    @Test
    @DisplayName("deleteKeep - keep not found")
    void deleteKeep_keepNotFound() {
        // given
        Member member = MemberFixture.create();
        Destination destination = KeepFixture.createDestinationWithContentId("content-1");

        given(memberRepository.findById(1L)).willReturn(Optional.of(member));
        given(destinationRepository.findByContentId("content-1")).willReturn(Optional.of(destination));
        given(queryDslKeepRepository.findByMemberAndDestination("content-1", 1L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> keepService.deleteKeep("content-1", 1L))
                .isInstanceOf(CommonException.class)
                .satisfies(ex -> assertThat(((CommonException) ex).getStatus()).isEqualTo(Status.NOT_FOUND_KEEP));
    }

    @Test
    @DisplayName("keeps - success")
    void keeps_success() {
        // given
        Member member = MemberFixture.create();
        Page<KeepElement> page = new PageImpl<>(List.of());

        given(memberRepository.findById(1L)).willReturn(Optional.of(member));
        given(queryDslKeepRepository.fetchKeeps(any(Long.class), any(Pageable.class))).willReturn(page);

        // when
        ApiResponse result = keepService.keeps(1L, 1, 10);

        // then
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("detailKeep - success")
    void detailKeep_success() {
        // given
        Member member = MemberFixture.create();
        Destination destination = KeepFixture.createDestinationWithContentId("content-1");
        KeepElement keepElement = mock(KeepElement.class);

        given(memberRepository.findById(1L)).willReturn(Optional.of(member));
        given(destinationRepository.findByContentId("content-1")).willReturn(Optional.of(destination));
        given(queryDslKeepRepository.fetchKeep("content-1", 1L)).willReturn(keepElement);

        // when
        ApiResponse result = keepService.detailKeep("content-1", 1L);

        // then
        assertThat(result).isNotNull();
    }
}
