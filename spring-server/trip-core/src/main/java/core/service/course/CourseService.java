package core.service.course;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import core.infra.projection.course.CourseDetailResponse;
import core.infra.projection.course.CourseResponse;
import core.infra.projection.course.CourseReviewResponse;
import core.infra.projection.course.CourseSpotDto;
import core.domain.entity.address.Address;
import core.domain.entity.course.Course;
import core.domain.entity.coursespot.CourseSpot;
import core.domain.entity.destination.Destination;
import core.infra.jpa.course.CourseRepository;
import core.infra.jpa.course.CourseReviewRepository;
import core.infra.jpa.course.CourseSpotRepository;
import core.infra.jpa.destination.DestinationRepository;
import core.infra.jpa.address.AddressRepository;
import core.infra.querydsl.course.QueryDslCourseRepository;
import core.infra.querydsl.course.QueryDslCourseSpotRepository;
import core.infra.jdbc.destination.JdbcDestinationRepository;
import core.api.response.ApiResponse;
import core.api.response.ApiDataResponse;
import core.common.Status;
import core.common.exception.CommonException;
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
public class CourseService {
    private final DestinationRepository destinationRepository;
    private final CourseRepository courseRepository;
    private final CourseSpotRepository courseSpotRepository;
    private final AddressRepository addressRepository;
    private final CourseReviewRepository courseReviewRepository;
    private final QueryDslCourseRepository queryDslCourseRepository;
    private final QueryDslCourseSpotRepository queryDslCourseSpotRepository;
    private final JdbcDestinationRepository jdbcDestinationRepository;
    @Value("${tourapi.service-key}")
    private String serviceKey;
    @Value("${tourapi.base-url}")
    private String baseUrl;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final RestTemplate restTemplate = new RestTemplate();
    public void saveCourseData() throws Exception{
        if(courseRepository.count()>0) return;
        Map<String, String> addressNameCache = new HashMap<>();
        for (Address address : addressRepository.findAll()) {
            String key = address.getAreaCode() + "_" + (address.getSigunguCode() == null ? "" : address.getSigunguCode());
            addressNameCache.put(key, address.getName());
        }
        List<Destination> courseList = jdbcDestinationRepository.findAllCourses();
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

    public ApiResponse recommendCourses(){
        List<Course> randomCourses = queryDslCourseRepository.findRandomCourses();
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
        return ApiDataResponse.of(response, Status.SUCCESS);
    }

    public ApiResponse getCourseDetails(Long courseId) {
        List<CourseSpot> spots = queryDslCourseSpotRepository.findByCourseId(courseId);
        Course course = courseRepository.findById(courseId).orElseThrow(()->new CommonException(Status.NOT_FOUND_COURSE));
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

        CourseDetailResponse result = CourseDetailResponse.builder()
                .courseId(courseId)
                .title(course.getTitle())
                .overview(course.getOverview())
                .spots(spotDtoList)
                .likeCount(course.getLikeCount())
                .rating(course.getRating())
                .reviewCount(course.getReviewCount())
                .reviewList(reviewList)
                .build();
        return ApiDataResponse.of(result, Status.SUCCESS);
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
