package core.service.kindplace;

import core.api.response.ApiResponse;
import core.api.response.ApiDataResponse;
import core.infra.projection.kindplace.ApiKindPlaces;
import core.common.Status;
import core.domain.entity.kindplace.KindPlace;
import core.infra.jpa.kindplace.KindPlaceRepository;
import core.infra.querydsl.kindplace.QueryDslKindPlaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class KindPlaceService {
    private final KindPlaceRepository kindPlaceRepository;
    private final QueryDslKindPlaceRepository queryDslKindPlaceRepository;
    @Value("${kindapi.service-key}")
    private String serviceKey;
    @Value("${kindapi.base-url}")
    private String baseUrl;
    private final RestTemplate restTemplate = new RestTemplate();


    public ApiResponse fetchKindPlaces(String address,Integer pageNum) {
        PageRequest pageRequest = PageRequest.of(pageNum - 1, 10);
        List<KindPlace> list = queryDslKindPlaceRepository.fetchKindPlaces(address,pageRequest);
        return ApiDataResponse.of(list, Status.SUCCESS);
    }

    public void saveKindPlace(){
        if (kindPlaceRepository.count() > 0) {
            return;
        }
        List<KindPlace> list = new ArrayList<>();
        for(int i= 1;i<11;i++){
            String url = baseUrl +"?page="+i+"&perPage=1000&returnType=json&"+
                    "serviceKey=" + serviceKey;
            ApiKindPlaces apiKindPlaces = fetchData(url);
            apiKindPlaces.getData()
                    .stream()
                    .forEach(apiKindPlace -> {
                        list.add(KindPlace.of(apiKindPlace));
                    });

        }
        kindPlaceRepository.saveAll(list);

    }

    private ApiKindPlaces fetchData(String url){
        try {
            restTemplate.getMessageConverters()
                    .add(0, new StringHttpMessageConverter(StandardCharsets.UTF_8));
            URI uri = new URI(url);
            return restTemplate.getForObject(uri, ApiKindPlaces.class);

        } catch (Exception e) {
            throw new RuntimeException("API 호출 실패: " + url,e);
        }
    }
}
