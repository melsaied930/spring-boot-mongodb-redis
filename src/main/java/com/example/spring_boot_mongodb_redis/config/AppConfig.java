package com.example.spring_boot_mongodb_redis.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
@ConfigurationProperties(prefix = "app")
@Data
public class AppConfig {
    private String apiUrl;
    private String userDataFile;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}