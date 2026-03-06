package com.tripplannerai;

import com.tripplannerai.dto.response.kindplace.ApiKindPlaces;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;

@SpringBootTest
@Transactional
class TripplannerAiApplicationTests {

    @Value("${kindapi.service-key}")
    private String serviceKey;
    @Value("${kindapi.base-url}")
    private String baseUrl;
    private final RestTemplate restTemplate = new RestTemplate();
    @Test
    void contextLoads() throws URISyntaxException {
        String url = baseUrl +"?page=11&perPage=1000&returnType=json&"+
                "serviceKey=" + serviceKey;
        restTemplate.getMessageConverters()
                .add(0, new StringHttpMessageConverter(StandardCharsets.UTF_8));
        URI uri = new URI(url);

    }



}
