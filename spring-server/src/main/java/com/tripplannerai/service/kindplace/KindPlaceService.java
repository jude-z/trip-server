package com.tripplannerai.service.kindplace;

import com.tripplannerai.dto.response.kindplace.ApiKindPlaces;
import com.tripplannerai.dto.response.kindplace.FetchKindPlaces;
import com.tripplannerai.entity.kindplace.KindPlace;
import com.tripplannerai.repository.address.AddressRepository;
import com.tripplannerai.repository.kindplace.KindPlaceRepository;
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

import static com.tripplannerai.util.ConstClass.*;

@Service
@RequiredArgsConstructor
public class KindPlaceService {
    private final KindPlaceRepository kindPlaceRepository;
    @Value("${kindapi.service-key}")
    private String serviceKey;
    @Value("${kindapi.base-url}")
    private String baseUrl;
    private final RestTemplate restTemplate = new RestTemplate();


    public FetchKindPlaces fetchKindPlaces(String address,Integer pageNum) {
        PageRequest pageRequest = PageRequest.of(pageNum - 1, 10);
        List<KindPlace> list = kindPlaceRepository.fetchKindPlaces(address,pageRequest);
        return FetchKindPlaces.of(SUCCESS_CODE,SUCCESS_MESSAGE,list);
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
