package com.tripplannerai.service.myplan;

import com.tripplannerai.dto.response.plan.PlanResponseDto;
import com.tripplannerai.dto.response.recommend.DayScheduleDto;

import java.util.List;

public interface PlanService {
    List<PlanResponseDto> getPlanListByUsername(String username);

    DayScheduleDto getDayScheduleByItineraryId(Long itineraryId);

    void deletePlanByPlanId(Long planId);
}
