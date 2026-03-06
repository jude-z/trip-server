package com.tripplannerai.service.address;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tripplannerai.entity.address.Address;
import com.tripplannerai.mapper.AddressFactory;
import com.tripplannerai.repository.address.AddressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AddressServiceImplement implements AddressService{
    private final AddressRepository addressRepository;

    @Value("${tourapi.service-key}")
    private String serviceKey;
    @Value("${tourapi.base-url}")
    private String baseUrl;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void saveAddressFromApi() throws Exception{
        if (addressRepository.count() > 0) {
            return;
        }
        String areaCodeUrl = baseUrl + "/areaCode2?numOfRows=100&pageNo=1&MobileOS=WEB&MobileApp=AppTest&_type=json&serviceKey=" + serviceKey;
        JsonNode areas = fetchData(areaCodeUrl).path("response").path("body").path("items").path("item");

        if (areas.isMissingNode()) {
            throw new RuntimeException("지역 코드 데이터 가져오기 실패");
        }

        for (JsonNode area : areas) {
            String areaCode = area.path("code").asText();
            String areaName = area.path("name").asText();
            addressRepository.save(AddressFactory.of(areaCode,areaName,null));
            String sigunguUrl = baseUrl + "/areaCode2?serviceKey=" + serviceKey +
                    "&areaCode=" + areaCode +
                    "&numOfRows=1000&pageNo=1&MobileOS=ETC&MobileApp=AppTest&_type=json";
            JsonNode sigungus = fetchData(sigunguUrl).path("response").path("body").path("items").path("item");

            List<Address> addresses = new ArrayList<>();
            for (JsonNode sigungu : sigungus) {
                String sigunguName = sigungu.path("name").asText();
                String sigunguCode = sigungu.path("code").asText();
                addresses.add(AddressFactory.of(areaCode,sigunguName,sigunguCode));
            }
            addressRepository.saveAll(addresses);
        }

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
}
