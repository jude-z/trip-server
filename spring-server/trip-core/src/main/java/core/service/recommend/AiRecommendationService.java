package core.service.recommend;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import core.api.request.recommend.RecommendRequestDto;
import core.api.request.recommend.SaveRecommendRequest;
import core.api.response.ApiResponse;
import core.api.response.ApiDataResponse;
import core.infra.projection.recommend.DayScheduleDto;
import core.infra.projection.recommend.RecommendationResponse;
import core.infra.projection.recommend.SpotDto;
import core.domain.entity.destination.Destination;
import core.domain.entity.itinerary.Itinerary;
import core.domain.entity.member.Member;
import core.domain.entity.plan.Plan;
import core.domain.entity.tourspot.TourSpot;
import core.api.response.ApiStatusResponse;
import core.common.Status;
import core.common.exception.CommonException;
import core.infra.jpa.destination.DestinationRepository;
import core.infra.jpa.member.MemberRepository;
import core.infra.jpa.plan.PlanRepository;
import core.infra.querydsl.member.QueryDslMemberRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AiRecommendationService {
    private final MemberRepository memberRepository;
    private final PlanRepository planRepository;
    private final DestinationRepository destinationRepository;
    private final QueryDslMemberRepository queryDslMemberRepository;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final Logger logger = LoggerFactory.getLogger(AiRecommendationService.class);
    @Value("${fastAPI.url}")
    private String FASTAPI_URL;

    public ApiResponse getRecommendation(RecommendRequestDto requestDto) throws JsonProcessingException {
        long diffInMilliSeconds = Math.abs(requestDto.getEndDate().getTime()-requestDto.getStartDate().getTime());
        int days = (int) (diffInMilliSeconds / (1000 * 60 * 60 * 24))+1;
        // HTTP 요청 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
        headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

        Map<String, Object> jsonRequest = new HashMap<>();
        jsonRequest.put("area_code", requestDto.getAreaCode());
        jsonRequest.put("sigungu_code", requestDto.getSigunguCode()!=null? requestDto.getSigunguCode():"");
        jsonRequest.put("category_codes", requestDto.getCategoryCodes());
        jsonRequest.put("days", days);
        String jsonBody = objectMapper.writeValueAsString(jsonRequest);

        HttpEntity<String> entity = new HttpEntity<>(jsonBody,headers);

        // POST 요청 보내기
        ResponseEntity<RecommendationResponse> response = restTemplate.exchange(FASTAPI_URL, HttpMethod.POST, entity, RecommendationResponse.class );

        // 응답 본문 반환
        return ApiDataResponse.of(response.getBody(), Status.SUCCESS);
    }

    @Transactional
    public ApiResponse saveRecommendation(SaveRecommendRequest request, String email){
        Date startDate = request.getStartDate();
        Date endDate = request.getEndDate();
        RecommendationResponse response = request.getResponse();

        Member member = queryDslMemberRepository.findByEmail(email).orElseThrow(() -> new CommonException(Status.NOT_FOUND_MEMBER));
        Plan plan = Plan.builder()
                .member(member)
                .startDate(startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime())
                .endDate(endDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime())
                .areaCode(response.getAreaCode())
                .itineraries(new ArrayList<>())
                .build();

        int dayCount = 1;
        for (int i =1;i<=response.getSchedule().size();i++) {
            String dayKey = "day_"+i;
            DayScheduleDto daySchedule = response.getSchedule().get(dayKey);
            Itinerary itinerary = Itinerary.builder()
                    .day(dayCount++)
                    .plan(plan)
                    .tourSpots(new ArrayList<>())
                    .build();
            plan.getItineraries().add(itinerary);

            int order = 1;
            for (SpotDto spot : daySchedule.getSpots()) {
                TourSpot tourSpot = TourSpot.builder()
                        .order(order++)
                        .destination(findDestinationById(spot.getDestinationId()))
                        .itinerary(itinerary)
                        .build();
                itinerary.getTourSpots().add(tourSpot);
            }
            if (daySchedule.getAccommodation() != null) {
                TourSpot accommodation = TourSpot.builder()
                        .order(order)
                        .destination(findDestinationById(daySchedule.getAccommodation().getDestinationId()))
                        .itinerary(itinerary)
                        .build();
                itinerary.getTourSpots().add(accommodation);
            }
        }
        planRepository.save(plan);
        return ApiStatusResponse.of(Status.SUCCESS);
    }


    private Destination findDestinationById(String destinationId) {
        return destinationRepository.findById(Long.parseLong(destinationId))
                .orElseThrow(() -> new CommonException(Status.NOT_FOUND_DESTINATION));
    }

}

