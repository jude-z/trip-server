package com.tripplannerai.service.myplan;

import com.tripplannerai.dto.response.plan.PlanResponseDto;
import com.tripplannerai.dto.response.recommend.DayScheduleDto;
import com.tripplannerai.dto.response.recommend.SpotDto;
import com.tripplannerai.entity.member.Member;
import com.tripplannerai.entity.plan.Plan;
import com.tripplannerai.entity.tourspot.TourSpot;
import com.tripplannerai.common.exception.member.NotFoundMemberException;
import com.tripplannerai.repository.itinerary.ItineraryRepository;
import com.tripplannerai.repository.member.MemberRepository;
import com.tripplannerai.repository.plan.PlanRepository;
import com.tripplannerai.repository.tourspot.TourSpotRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlanServiceImpl implements PlanService{

    private final PlanRepository planRepository;
    private final MemberRepository memberRepository;
    private final ItineraryRepository itineraryRepository;
    private final TourSpotRepository tourSpotRepository;

    @Override
    public List<PlanResponseDto> getPlanListByUsername(String username) {
        Member member = memberRepository.findByEmail(username).orElseThrow(() -> new NotFoundMemberException("not found member"));
        List<Plan> planList = planRepository.findAllByMember_Id(member.getId());
        return PlanResponseDto.fromEntities(planList);
    }

    @Override
    public DayScheduleDto getDayScheduleByItineraryId(Long itineraryId) {
        List<TourSpot> tourSpots = tourSpotRepository.findAllByItineraryIdWithDetails(itineraryId);
        List<SpotDto> spots = new ArrayList<>();
        for (int i = 0; i < tourSpots.size() ; i++) {
            spots.add(SpotDto.fromEntity(tourSpots.get(i)));
        }
        return DayScheduleDto.builder()
                .spots(spots)
                .build();
    }

    @Transactional
    @Override
    public void deletePlanByPlanId(Long planId) {
        planRepository.deleteById(planId);
    }


}
