package com.mockserver.webhook;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

//@EnableScheduling
@Configuration
public class WebHookConfig {

    @Bean
    public ScheduledExecutorService webhookTaskScheduler(){
        return Executors.newSingleThreadScheduledExecutor();
    }

    @Bean
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }
}
