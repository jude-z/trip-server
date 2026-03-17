package core.api.controller.destination;

import com.fasterxml.jackson.core.JsonProcessingException;
import core.api.common.resolver.annotation.Username;
import core.api.request.recommend.RecommendRequestDto;
import core.api.request.recommend.SaveRecommendRequest;
import core.api.response.ApiResponse;
import core.service.recommend.AiRecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/recommend")
public class AiRecommendationController {
    private final AiRecommendationService recommendationService;

    @PostMapping
    public ResponseEntity<ApiResponse> getRecommendation(@RequestBody RecommendRequestDto requestDto, @Username String email) throws JsonProcessingException {
        return ResponseEntity.ok(recommendationService.getRecommendation(requestDto));
    }

    @PostMapping("/save")
    public ResponseEntity<ApiResponse> saveRecommendation(@RequestBody SaveRecommendRequest request, @Username String email) {
        return ResponseEntity.ok(recommendationService.saveRecommendation(request, email));
    }
}
