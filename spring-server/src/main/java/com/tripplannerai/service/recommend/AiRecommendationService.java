package com.tripplannerai.service.recommend;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.tripplannerai.dto.request.recommend.RecommendRequestDto;
import com.tripplannerai.dto.request.recommend.SaveRecommendRequest;
import com.tripplannerai.dto.response.recommend.AiSaveResponse;
import com.tripplannerai.dto.response.recommend.RecommendationResponse;

public interface AiRecommendationService {
    RecommendationResponse getRecommendation(RecommendRequestDto requestDto) throws JsonProcessingException;
    AiSaveResponse saveRecommendation(SaveRecommendRequest request, String email);
}
