package core.service.myplan;

import core.api.response.ApiResponse;
import core.common.Status;
import core.common.exception.CommonException;
import core.domain.entity.destination.Destination;
import core.domain.entity.itinerary.Itinerary;
import core.domain.entity.member.Member;
import core.domain.entity.plan.Plan;
import core.domain.entity.tourspot.TourSpot;
import core.fixture.GroupFixture;
import core.fixture.MemberFixture;
import core.fixture.PlanFixture;
import core.infra.jpa.itinerary.ItineraryRepository;
import core.infra.jpa.member.MemberRepository;
import core.infra.jpa.plan.PlanRepository;
import core.infra.jpa.tourspot.TourSpotRepository;
import core.infra.querydsl.member.QueryDslMemberRepository;
import core.infra.querydsl.plan.QueryDslPlanRepository;
import core.infra.querydsl.tourspot.QueryDslTourSpotRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PlanServiceTest {

    @InjectMocks
    private PlanService planService;

    @Mock
    private PlanRepository planRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private ItineraryRepository itineraryRepository;

    @Mock
    private TourSpotRepository tourSpotRepository;

    @Mock
    private QueryDslMemberRepository queryDslMemberRepository;

    @Mock
    private QueryDslPlanRepository queryDslPlanRepository;

    @Mock
    private QueryDslTourSpotRepository queryDslTourSpotRepository;

    @Test
    @DisplayName("getPlanListByUsername - success")
    void getPlanListByUsername_success() {
        // given
        String username = "test@example.com";
        Member member = MemberFixture.create();
        Plan plan = PlanFixture.createPlan(member);

        given(queryDslMemberRepository.findByEmail(username)).willReturn(Optional.of(member));
        given(queryDslPlanRepository.findAllByMemberId(member.getId())).willReturn(List.of(plan));

        // when
        ApiResponse result = planService.getPlanListByUsername(username);

        // then
        assertThat(result).isNotNull();
        verify(queryDslMemberRepository).findByEmail(username);
        verify(queryDslPlanRepository).findAllByMemberId(member.getId());
    }

    @Test
    @DisplayName("getPlanListByUsername - member not found throws NOT_FOUND_MEMBER")
    void getPlanListByUsername_memberNotFound() {
        // given
        given(queryDslMemberRepository.findByEmail("notfound@example.com")).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> planService.getPlanListByUsername("notfound@example.com"))
                .isInstanceOf(CommonException.class)
                .satisfies(ex -> assertThat(((CommonException) ex).getStatus()).isEqualTo(Status.NOT_FOUND_MEMBER));
    }

    @Test
    @DisplayName("getDayScheduleByItineraryId - success")
    void getDayScheduleByItineraryId_success() {
        // given
        Long itineraryId = 1L;
        Member member = MemberFixture.create();
        Plan plan = PlanFixture.createPlan(member);
        Itinerary itinerary = PlanFixture.createItinerary(plan);
        Destination destination = GroupFixture.createDestination();
        TourSpot tourSpot = PlanFixture.createTourSpot(itinerary, destination);

        given(queryDslTourSpotRepository.findAllByItineraryIdWithDetails(itineraryId)).willReturn(List.of(tourSpot));

        // when
        ApiResponse result = planService.getDayScheduleByItineraryId(itineraryId);

        // then
        assertThat(result).isNotNull();
        verify(queryDslTourSpotRepository).findAllByItineraryIdWithDetails(itineraryId);
    }

    @Test
    @DisplayName("deletePlanByPlanId - success")
    void deletePlanByPlanId_success() {
        // when
        planService.deletePlanByPlanId(1L);

        // then
        verify(planRepository).deleteById(1L);
    }
}
