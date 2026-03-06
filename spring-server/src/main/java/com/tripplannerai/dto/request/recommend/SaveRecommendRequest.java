package com.tripplannerai.dto.request.recommend;

import com.tripplannerai.dto.response.recommend.RecommendationResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SaveRecommendRequest {
    private Date startDate;
    private Date endDate;
    private RecommendationResponse response;
}
