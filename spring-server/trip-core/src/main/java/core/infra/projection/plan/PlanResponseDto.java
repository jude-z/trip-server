package core.infra.projection.plan;

import core.domain.entity.plan.Plan;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlanResponseDto {
    private Long planId;
    private LocalDate startDate;
    private LocalDate endDate;
    private String areaCode;
    private List<ItineraryResponseDto> itineraries;

    public static PlanResponseDto fromEntity(Plan plan) {
        return PlanResponseDto.builder()
                .planId(plan.getPlanId())
                .startDate(plan.getStartDate().toLocalDate())
                .endDate(plan.getEndDate().toLocalDate())
                .areaCode(plan.getAreaCode())
                .itineraries(ItineraryResponseDto.fromEntities(plan.getItineraries()))
                .build();
    }
    public static List<PlanResponseDto> fromEntities(List<Plan> plans) {
        return plans.stream()
                .map(PlanResponseDto::fromEntity) // Plan → PlanResponseDto 변환
                .toList();
    }
}
