package com.tripplannerai.controller.destination;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.tripplannerai.common.annotation.Username;
import com.tripplannerai.dto.request.recommend.RecommendRequestDto;
import com.tripplannerai.dto.request.recommend.SaveRecommendRequest;
import com.tripplannerai.dto.response.recommend.AiSaveResponse;
import com.tripplannerai.dto.response.recommend.RecommendationResponse;
import com.tripplannerai.service.myplan.PlanService;
import com.tripplannerai.service.recommend.AiRecommendationService;
import com.tripplannerai.util.ConstClass;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

import static com.tripplannerai.util.ConstClass.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/recommend")
public class AiRecommendationController {
    private final AiRecommendationService recommendationService;
    private final PlanService planService;

    @PostMapping
    public ResponseEntity<RecommendationResponse> getRecommendation(@RequestBody RecommendRequestDto requestDto, @Username String email) throws JsonProcessingException {
        RecommendationResponse recommendations = recommendationService.getRecommendation(requestDto);
        return ResponseEntity.ok(recommendations);
    }

    @PostMapping("/save")
    public ResponseEntity<AiSaveResponse> saveRecommendation(@RequestBody SaveRecommendRequest request, @Username String email) {
        AiSaveResponse aiSaveResponse = recommendationService.saveRecommendation(request,email);
        return new ResponseEntity<>(aiSaveResponse, HttpStatus.CREATED);
    }

}
