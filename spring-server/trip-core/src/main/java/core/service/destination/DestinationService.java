package core.service.destination;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import core.domain.entity.address.Address;
import core.domain.entity.category.Category;
import core.domain.entity.destination.Destination;
import core.domain.entity.member.Member;
import core.domain.entity.searchlog.SearchLog;
import core.domain.entity.viewlog.ViewLog;
import core.domain.mapper.DestinationFactory;
import core.domain.mapper.SearchLogFactory;
import core.domain.mapper.ViewLogFactory;
import core.infra.jpa.destination.DestinationRepository;
import core.infra.jpa.member.MemberRepository;
import core.infra.jpa.searchlog.SearchLogRepository;
import core.infra.jpa.viewlog.ViewLogRepository;
import core.infra.jpa.address.AddressRepository;
import core.infra.jpa.category.CategoryRepository;
import core.infra.jdbc.destination.JdbcDestinationRepository;
import core.api.response.ApiDataResponse;
import core.api.response.ApiResponse;
import core.common.Status;
import core.common.exception.CommonException;
import core.common.util.PageLimitCalculator;
import core.infra.projection.destination.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class DestinationService {

    private final DestinationRepository destinationRepository;
    private final CategoryRepository categoryRepository;
    private final AddressRepository addressRepository;
    private final MemberRepository memberRepository;
    private final ViewLogRepository viewLogRepository;
    private final SearchLogRepository searchLogRepository;
    private final JdbcDestinationRepository jdbcDestinationRepository;
    @Value("${tourapi.service-key}")
    private String serviceKey;
    @Value("${tourapi.base-url}")
    private String baseUrl;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final RestTemplate restTemplate = new RestTemplate();

    public void saveDestinationFromApi() throws Exception {
        if (destinationRepository.count() > 0) {
            return;
        }
        Map<String, Address> addressCache = addressRepository.findAll().stream()
                .collect(Collectors.toMap(
                        addr -> addr.getAreaCode() + "_" + addr.getSigunguCode(),
                        Function.identity()
                ));

        Map<String, Category> categoryCache = categoryRepository.findAll().stream()
                .collect(Collectors.toMap(
                        cat -> buildCategoryKey(cat),
                        Function.identity()
                ));

        int pageNo = 1;
        int numOfRows = 1000;
        boolean hasMore = true;
        while (hasMore) {
            String destinationUrl = baseUrl + "/areaBasedList2?_type=json&serviceKey=" + serviceKey +
                    "&numOfRows="+numOfRows+"&pageNo="+pageNo+"&MobileOS=ETC&MobileApp=AppTest";
            JsonNode responseBody = fetchData(destinationUrl).get("response").path("body");
            JsonNode destinationList = responseBody.path("items").path("item");


            List<Destination> batch =  new ArrayList<>();

            for (JsonNode destinationNode : destinationList) {
                Destination destination = DestinationFactory.fromApiResponse(destinationNode);
                String cat1 = destinationNode.path("cat1").asText();
                if(cat1.equals("25")) continue; // 여행코스는 저장하지 않음
                String cat2 = destinationNode.path("cat2").asText();
                String cat3 = destinationNode.path("cat3").asText();
                String categoryKey = cat1 + "_" + cat2 + "_" + cat3;
                Category category = categoryCache.getOrDefault(categoryKey, null);
                String areaCode = destinationNode.path("areacode").asText();
                String sigunguCode = destinationNode.path("sigungucode").asText();
                String addressKey = areaCode + "_" + sigunguCode;
                Address address = addressCache.getOrDefault(addressKey, null);

                destination.setCategory(category);
                destination.setAddress(address);
                batch.add(destination);
            }
            destinationRepository.saveAll(batch);

            int totalCount = Integer.parseInt(responseBody.path("totalCount").asText());
            int totalPages = (int) Math.ceil((double) totalCount/numOfRows);
            if (pageNo < totalPages) {
                pageNo++;
            } else {
                hasMore = false;
            }
        }


    }

    public ApiResponse fetchDestination(Long destinationId, Long id) {
        Member member = memberRepository.findById(id).orElseThrow(() -> new CommonException(Status.NOT_FOUND_MEMBER));
        Destination destination = destinationRepository.findById(destinationId)
                .orElseThrow(()-> new CommonException(Status.NOT_FOUND_DESTINATION));
        ViewLog viewLog = ViewLogFactory.from(destination, member);
        viewLogRepository.save(viewLog);
        return ApiDataResponse.of(destination, Status.SUCCESS);
    }

    public ApiResponse fetchDestinations(Integer page, Integer size, String name, Long id) {
        Member member = memberRepository.findById(id).orElseThrow(() -> new CommonException(Status.NOT_FOUND_MEMBER));
        if(StringUtils.hasText(name)){
            SearchLog searchLog = SearchLogFactory.from(name, member);
            searchLogRepository.save(searchLog);
        }
        int limit = size;
        int offset = (page-1)*limit;
        int pageLimit = PageLimitCalculator.calculatePageLimit(page, size, 10);
        List<DestinationQuery> destinationQueries = jdbcDestinationRepository.fetchDestinations(offset, limit);
        List<DestinationResponse> content = destinationQueries.stream().map(DestinationResponse::of).toList();
        int totalCount = jdbcDestinationRepository.fetchDestinationsCount(pageLimit);
        return ApiDataResponse.of(DestinationsResponse.of(content,totalCount), Status.SUCCESS);
    }

    public ApiResponse fetchDestinationByCategory(Integer page, Integer size, Long categoryId) {
        int limit = size;
        int offset = (page-1)*limit;
        int pageLimit = PageLimitCalculator.calculatePageLimit(page, size, 10);
        List<DestinationCategoryQuery> destinationCategoryQueries = jdbcDestinationRepository.fetchDestinationByCategory(offset, limit, categoryId);
        List<DestinationCategoryDto> content = toDestinationCategoryDto(destinationCategoryQueries);
        int totalCount = jdbcDestinationRepository.fetchDestinationsCategoryCount(categoryId,pageLimit);
        return ApiDataResponse.of(Map.of("content", content, "totalCount", totalCount), Status.SUCCESS);
    }

    private List<DestinationCategoryDto> toDestinationCategoryDto(List<DestinationCategoryQuery> destinationCategoryQueries) {
        return destinationCategoryQueries.stream()
                .map(query -> DestinationCategoryDto.of(query))
                .toList();
    }

    public ApiResponse fetchDestinationsByTotal() {
        List<DestinationTotalQuery> destinationQueries = jdbcDestinationRepository.fetchDestinationsByTotal();
        List<DestinationTotalDto> destinationTotalDtoList = toDestinationTotalDtoList(destinationQueries);
        Map<String, List<DestinationTotalDto>> map = groupByMiddleCategory(destinationTotalDtoList);
        return ApiDataResponse.of(map, Status.SUCCESS);
    }

    private List<DestinationTotalDto> toDestinationTotalDtoList(List<DestinationTotalQuery> destinationQueries) {
        return destinationQueries.stream()
                .map(DestinationTotalDto::of)
                .toList();
    }
    private  Map<String, List<DestinationTotalDto>> groupByMiddleCategory(List<DestinationTotalDto> destinationTotalDtoList) {


        return destinationTotalDtoList.stream()
                .collect(Collectors.groupingBy(DestinationTotalDto::getMiddleCategoryId));
    }

    public ApiResponse fetchMyDestinations(Integer page, Integer size, Long id) {
        return null;
    }

    public ApiResponse getDestinationsByContentIds(List<String> contentIds) {
        List<Destination> destinations = destinationRepository.findAllByContentIdIn(contentIds);
        List<DestinationInfoResponse> result = destinations.stream()
                .map(DestinationInfoResponse::fromEntity)
                .toList();
        return ApiDataResponse.of(result, Status.GET_DESTINATIONS_BY_CONTENT_ID);
    }

    private JsonNode fetchData(String url) throws Exception{
        try {
            restTemplate.getMessageConverters()
                    .add(0, new StringHttpMessageConverter(StandardCharsets.UTF_8));
            URI uri = new URI(url);
            String response = restTemplate.getForObject(uri, String.class);

            return objectMapper.readTree(response);
        } catch (Exception e) {
            throw new RuntimeException("API 호출 실패: " + url,e);
        }
    }
    private String buildCategoryKey(Category category) {
        StringBuilder sb = new StringBuilder();
        if (category.getCategory() != null) {
            if (category.getCategory().getCategory() != null) {
                sb.append(category.getCategory().getCategory().getCategoryCode()).append("_");
            }
            sb.append(category.getCategory().getCategoryCode()).append("_");
        }
        sb.append(category.getCategoryCode());
        return sb.toString();
    }
}
