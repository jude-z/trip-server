package com.tripplannerai.service.course;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tripplannerai.common.exception.course.NotFoundCourseException;
import com.tripplannerai.dto.response.course.CourseDetailResponse;
import com.tripplannerai.dto.response.course.CourseResponse;
import com.tripplannerai.dto.response.course.CourseReviewResponse;
import com.tripplannerai.dto.response.course.CourseSpotDto;
import com.tripplannerai.entity.address.Address;
import com.tripplannerai.entity.course.Course;
import com.tripplannerai.entity.coursespot.CourseSpot;
import com.tripplannerai.entity.destination.Destination;
import com.tripplannerai.repository.address.AddressRepository;
import com.tripplannerai.repository.course.CourseRepository;
import com.tripplannerai.repository.course.CourseReviewRepository;
import com.tripplannerai.repository.course.CourseSpotRepository;
import com.tripplannerai.repository.destination.DestinationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class CourseServiceImpl implements CourseService {
    private final DestinationRepository destinationRepository;
    private final CourseRepository courseRepository;
    private final CourseSpotRepository courseSpotRepository;
    private final AddressRepository addressRepository;
    private final CourseReviewRepository courseReviewRepository;
    @Value("${tourapi.service-key}")
    private String serviceKey;
    @Value("${tourapi.base-url}")
    private String baseUrl;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final RestTemplate restTemplate = new RestTemplate();
    @Override
    public void saveCourseData() throws Exception{
        if(courseRepository.count()>0) return;
        Map<String, String> addressNameCache = new HashMap<>();
        for (Address address : addressRepository.findAll()) {
            String key = address.getAreaCode() + "_" + (address.getSigunguCode() == null ? "" : address.getSigunguCode());
            addressNameCache.put(key, address.getName());
        }
        List<Destination> courseList = destinationRepository.findAllCourses();
        List<Course> courseBatch = new ArrayList<>();
        List<CourseSpot> courseSpotBatch = new ArrayList<>();
        for(Destination d : courseList) {
            String contentId = d.getContentId();
            String commonUrl = baseUrl + "/detailCommon2"+"?_type=json&ServiceKey=" + serviceKey +
                    "&contentTypeId=25&contentId=" + contentId +
                    "&MobileOS=ETC&MobileApp=AppTest&defaultYN=Y&firstImageYN=Y&areacodeYN=Y&catcodeYN=Y&addrinfoYN=Y&mapinfoYN=Y&overviewYN=Y";
            JsonNode responseCommon = fetchData(commonUrl).get("response").path("body").path("items").path("item").get(0);
            String title = responseCommon.path("title").asText();
            String overview = responseCommon.path("overview").asText();
            String areaCode = responseCommon.path("areacode").asText();
            String sigunguCode = responseCommon.path("sigungucode").asText();
            String key = areaCode + "_" + sigunguCode;
            String areaName = addressNameCache.getOrDefault(key,"");
            String detailUrl = baseUrl + "/detailIntro1"+"?_type=json&ServiceKey=" + serviceKey +
                    "&contentTypeId=25&contentId=" + contentId +
                    "&MobileOS=ETC&MobileApp=AppTest";
            JsonNode responseIntro = fetchData(detailUrl).get("response").path("body").path("items").path("item").get(0);
            String duration = responseIntro.path("taketime").asText();
            String distance = responseIntro.path("distance").asText();
            Course course = Course.builder()
                    .overview(overview)
                    .contentId(contentId)
                    .areaName(areaName)
                    .duration(duration)
                    .distance(distance)
                    .title(title)
                    .build();
            courseBatch.add(course);
            String courseSpotUrl = baseUrl + "/detailInfo1"+"?_type=json&ServiceKey=" + serviceKey +
                    "&contentTypeId=25&contentId=" + contentId +
                    "&MobileOS=ETC&MobileApp=AppTest";
            JsonNode responseSpots = fetchData(courseSpotUrl).get("response").path("body").path("items").path("item");

            int order = 1;
            for(JsonNode spot : responseSpots) {
                String spotContentId = spot.path("subcontentid").asText();
                String subOverview = spot.path("subdetailoverview").asText();
                if(spotContentId.isBlank()) continue;
                CourseSpot courseSpot = CourseSpot.builder()
                        .course(course)
                        .destinationContentId(spotContentId)
                        .subOverview(subOverview)
                        .orderInCourse(order++)
                        .build();
                courseSpotBatch.add(courseSpot);
            }
        }
        courseRepository.saveAll(courseBatch);
        courseSpotRepository.saveAll(courseSpotBatch);
    }

    @Override
    public List<CourseResponse> recommendCourses(){
        List<Course> randomCourses = courseRepository.findRandomCourses();
        List<CourseResponse> response = new ArrayList<>();
        for(Course course : randomCourses) {
            Destination d = destinationRepository.findByContentId(course.getContentId()).orElseThrow(()->new RuntimeException("not exists destination"));
            response.add(CourseResponse.builder()
                            .area(course.getAreaName())
                            .courseId(course.getCourseId())
                            .thumbnailUrl(d.getThumbnailImageUrl())
                            .likeCount(course.getLikeCount())
                            .rating(course.getRating())
                            .reviewCount(course.getReviewCount())
                            .contentId(course.getContentId())
                            .title(course.getTitle())
                    .build());
        }
        return response;
    }

    @Override
    public CourseDetailResponse getCourseDetails(Long courseId) {
        List<CourseSpot> spots = courseSpotRepository.findByCourseId(courseId);
        Course course = courseRepository.findById(courseId).orElseThrow(()->new NotFoundCourseException("Course not found"));
        List<CourseSpotDto> spotDtoList = new ArrayList<>();
        for(CourseSpot s : spots) {
            Optional<Destination> d = destinationRepository.findByContentId(s.getDestinationContentId());
            if(d.isEmpty()) continue;
            CourseSpotDto spotDto = CourseSpotDto.builder()
                    .order(s.getOrderInCourse())
                    .description(s.getSubOverview())
                    .imageUrl(d.get().getOriginImageUrl())
                    .title(d.get().getName())
                    .address(d.get().getAddr1())
                    .contentId(s.getDestinationContentId())
                    .build();
            spotDtoList.add(spotDto);
        }
        List<CourseReviewResponse> reviewList = courseReviewRepository.findByCourse(course)
                .stream()
                .map(CourseReviewResponse::fromEntity)
                .toList();

        return CourseDetailResponse.builder()
                .courseId(courseId)
                .title(course.getTitle())
                .overview(course.getOverview())
                .spots(spotDtoList)
                .likeCount(course.getLikeCount())
                .rating(course.getRating())
                .reviewCount(course.getReviewCount())
                .reviewList(reviewList)
                .build();
    }


    private JsonNode fetchData(String url) throws Exception{
        try {
            restTemplate.getMessageConverters()
                    .add(0, new StringHttpMessageConverter(StandardCharsets.UTF_8));
            URI uri = new URI(url);
            String response = restTemplate.getForObject(uri, String.class);
            if (!response.trim().startsWith("{")) {
                throw new RuntimeException("Invalid JSON response");
            }
            return objectMapper.readTree(response);
        } catch (Exception e) {
            throw new RuntimeException("API 호출 실패: " + url,e);
        }
    }



}
