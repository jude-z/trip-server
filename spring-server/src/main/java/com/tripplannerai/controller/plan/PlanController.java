package com.tripplannerai.controller.plan;


import com.tripplannerai.common.annotation.Username;
import com.tripplannerai.dto.response.plan.DeletePlanResponse;
import com.tripplannerai.dto.response.plan.PlanResponseDto;
import com.tripplannerai.dto.response.recommend.DayScheduleDto;
import com.tripplannerai.service.myplan.PlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.tripplannerai.util.ConstClass.SUCCESS_CODE;
import static com.tripplannerai.util.ConstClass.SUCCESS_MESSAGE;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/plan")
public class PlanController {
    private final PlanService planService;

    @GetMapping
    public ResponseEntity<List<PlanResponseDto>> getTourPlansByEmail(@Username String email) {
        return ResponseEntity.ok(planService.getPlanListByUsername(email));
    }

    @DeleteMapping("/{planId}")
    public ResponseEntity<DeletePlanResponse> deletePlan(@PathVariable("planId") Long planId) {
        planService.deletePlanByPlanId(planId);
        return ResponseEntity.ok(DeletePlanResponse.of(SUCCESS_CODE, SUCCESS_MESSAGE));
    }

    @GetMapping("/detail/{itineraryId}")
    public ResponseEntity<DayScheduleDto> getDayScheduleDetails(@PathVariable("itineraryId") Long itineraryId) {
        DayScheduleDto response = planService.getDayScheduleByItineraryId(itineraryId);
        return ResponseEntity.ok(response);
    }

}
