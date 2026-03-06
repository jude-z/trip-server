package com.tripplannerai.dto.response.recommend;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecommendationResponse {
    private Map<String, DayScheduleDto> schedule;
    private String message;
    @JsonProperty("area_code")
    private String areaCode;
}

