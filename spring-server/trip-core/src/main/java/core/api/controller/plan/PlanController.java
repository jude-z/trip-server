package core.api.controller.plan;

import core.api.common.resolver.annotation.Username;
import core.api.response.ApiResponse;
import core.api.response.ApiStatusResponse;
import core.common.Status;
import core.service.myplan.PlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/plan")
public class PlanController {
    private final PlanService planService;

    @GetMapping
    public ResponseEntity<ApiResponse> getTourPlansByEmail(@Username String email) {
        return ResponseEntity.ok(planService.getPlanListByUsername(email));
    }

    @DeleteMapping("/{planId}")
    public ResponseEntity<ApiResponse> deletePlan(@PathVariable("planId") Long planId) {
        planService.deletePlanByPlanId(planId);
        return ResponseEntity.ok(ApiStatusResponse.of(Status.SUCCESS));
    }

    @GetMapping("/detail/{itineraryId}")
    public ResponseEntity<ApiResponse> getDayScheduleDetails(@PathVariable("itineraryId") Long itineraryId) {
        return ResponseEntity.ok(planService.getDayScheduleByItineraryId(itineraryId));
    }
}
