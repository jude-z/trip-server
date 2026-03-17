package core.service.myplan;

import core.api.response.ApiResponse;
import core.api.response.ApiDataResponse;
import core.infra.projection.plan.PlanResponseDto;
import core.infra.projection.recommend.DayScheduleDto;
import core.infra.projection.recommend.SpotDto;
import core.common.Status;
import core.common.exception.CommonException;
import core.domain.entity.member.Member;
import core.domain.entity.plan.Plan;
import core.domain.entity.tourspot.TourSpot;
import core.infra.jpa.itinerary.ItineraryRepository;
import core.infra.jpa.member.MemberRepository;
import core.infra.jpa.plan.PlanRepository;
import core.infra.jpa.tourspot.TourSpotRepository;
import core.infra.querydsl.member.QueryDslMemberRepository;
import core.infra.querydsl.plan.QueryDslPlanRepository;
import core.infra.querydsl.tourspot.QueryDslTourSpotRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlanService {

    private final PlanRepository planRepository;
    private final MemberRepository memberRepository;
    private final ItineraryRepository itineraryRepository;
    private final TourSpotRepository tourSpotRepository;
    private final QueryDslMemberRepository queryDslMemberRepository;
    private final QueryDslPlanRepository queryDslPlanRepository;
    private final QueryDslTourSpotRepository queryDslTourSpotRepository;

    public ApiResponse getPlanListByUsername(String username) {
        Member member = queryDslMemberRepository.findByEmail(username).orElseThrow(() -> new CommonException(Status.NOT_FOUND_MEMBER));
        List<Plan> planList = queryDslPlanRepository.findAllByMemberId(member.getId());
        return ApiDataResponse.of(PlanResponseDto.fromEntities(planList), Status.SUCCESS);
    }

    public ApiResponse getDayScheduleByItineraryId(Long itineraryId) {
        List<TourSpot> tourSpots = queryDslTourSpotRepository.findAllByItineraryIdWithDetails(itineraryId);
        List<SpotDto> spots = new ArrayList<>();
        for (int i = 0; i < tourSpots.size() ; i++) {
            spots.add(SpotDto.fromEntity(tourSpots.get(i)));
        }
        DayScheduleDto result = DayScheduleDto.builder()
                .spots(spots)
                .build();
        return ApiDataResponse.of(result, Status.SUCCESS);
    }

    @Transactional
    public void deletePlanByPlanId(Long planId) {
        planRepository.deleteById(planId);
    }


}
