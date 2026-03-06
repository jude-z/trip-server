package com.tripplannerai.service.destination;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tripplannerai.dto.response.destination.*;
import com.tripplannerai.entity.address.Address;
import com.tripplannerai.entity.category.Category;
import com.tripplannerai.entity.destination.Destination;
import com.tripplannerai.entity.member.Member;
import com.tripplannerai.entity.searchlog.SearchLog;
import com.tripplannerai.entity.viewlog.ViewLog;
import com.tripplannerai.common.exception.destination.NotFoundDDestinationException;
import com.tripplannerai.common.exception.member.NotFoundMemberException;
import com.tripplannerai.mapper.DestinationFactory;
import com.tripplannerai.mapper.SearchLogFactory;
import com.tripplannerai.mapper.ViewLogFactory;
import com.tripplannerai.repository.address.AddressRepository;
import com.tripplannerai.repository.category.CategoryRepository;
import com.tripplannerai.repository.destination.DestinationRepository;
import com.tripplannerai.repository.member.MemberRepository;
import com.tripplannerai.repository.searchlog.SearchLogRepository;
import com.tripplannerai.repository.viewlog.ViewLogRepository;
import com.tripplannerai.util.PageLimitCalculator;
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

import static com.tripplannerai.util.ConstClass.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class DestinationServiceImplement implements DestinationService {

    private final DestinationRepository destinationRepository;
    private final CategoryRepository categoryRepository;
    private final AddressRepository addressRepository;
    private final MemberRepository memberRepository;
    private final ViewLogRepository viewLogRepository;
    private final SearchLogRepository searchLogRepository;
    @Value("${tourapi.service-key}")
    private String serviceKey;
    @Value("${tourapi.base-url}")
    private String baseUrl;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final RestTemplate restTemplate = new RestTemplate();

    @Override
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

    @Override
    public DestinationResponse fetchDestination(Long destinationId, Long id) {
        Member member = memberRepository.findById(id).orElseThrow(() -> new NotFoundMemberException("not found member"));
        Destination destination = destinationRepository.findById(destinationId)
                .orElseThrow(()-> new NotFoundDDestinationException("not found Destination!!"));
        ViewLog viewLog = ViewLogFactory.from(destination, member);
        viewLogRepository.save(viewLog);
        return DestinationResponse.of(SUCCESS_CODE,SUCCESS_MESSAGE,destination);
    }

    @Override
    public DestinationsResponse fetchDestinations(Integer page, Integer size, String name, Long id) {
        Member member = memberRepository.findById(id).orElseThrow(() -> new NotFoundMemberException("not found member"));
        if(StringUtils.hasText(name)){
            SearchLog searchLog = SearchLogFactory.from(name, member);
            searchLogRepository.save(searchLog);
        }
        int limit = size;
        int offset = (page-1)*limit;
        int pageLimit = PageLimitCalculator.calculatePageLimit(page, size, 10);
        List<DestinationQuery> destinationQueries = destinationRepository.fetchDestinations(offset, limit);
        List<DestinationResponse> content = destinationQueries.stream().map(DestinationResponse::of).toList();
        int totalCount = destinationRepository.fetchDestinationsCount(pageLimit);
        return DestinationsResponse.of(content,totalCount);
    }

    @Override
    public DestinationsCategoryResponse fetchDestinationByCategory(Integer page, Integer size, Long categoryId) {
        int limit = size;
        int offset = (page-1)*limit;
        int pageLimit = PageLimitCalculator.calculatePageLimit(page, size, 10);
        List<DestinationCategoryQuery> destinationCategoryQueries = destinationRepository.fetchDestinationByCategory(offset, limit, categoryId);
        List<DestinationCategoryDto> content = toDestinationCategoryDto(destinationCategoryQueries);
        int totalCount = destinationRepository.fetchDestinationsCategoryCount(categoryId,pageLimit);
        return DestinationsCategoryResponse.of(SUCCESS_CODE,SUCCESS_MESSAGE,content,totalCount);
    }

    private List<DestinationCategoryDto> toDestinationCategoryDto(List<DestinationCategoryQuery> destinationCategoryQueries) {
        return destinationCategoryQueries.stream()
                .map(query -> DestinationCategoryDto.of(query))
                .toList();
    }

    @Override
    public TotalDestinationResponse fetchDestinationsByTotal() {
        List<DestinationTotalQuery> destinationQueries = destinationRepository.fetchDestinationsByTotal();
        List<DestinationTotalDto> destinationTotalDtoList = toDestinationTotalDtoList(destinationQueries);
        Map<String, List<DestinationTotalDto>> map = groupByMiddleCategory(destinationTotalDtoList);
        return TotalDestinationResponse.of(SUCCESS_CODE,SUCCESS_MESSAGE,map);
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

    @Override
    public DestinationsResponse fetchMyDestinations(Integer page, Integer size, Long id) {
        return null;
    }

    @Override
    public List<DestinationInfoResponse> getDestinationsByContentIds(List<String> contentIds) {
        List<Destination> destinations = destinationRepository.findAllByContentIdIn(contentIds);
        return destinations.stream()
                .map(DestinationInfoResponse::fromEntity)
                .toList();
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
