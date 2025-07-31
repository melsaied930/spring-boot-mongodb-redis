package com.example.spring_boot_mongodb_redis.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "app")
public class AppProperties {
    private String apiUrl;
    private String userDataFile;
}
