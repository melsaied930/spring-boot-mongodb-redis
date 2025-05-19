package com.example.spring_boot_mongodb_redis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@EnableMongoRepositories(basePackages = "com.example.spring_boot_mongodb_redis.repository")


@EnableCaching
@SpringBootApplication
public class SpringBootMongodbRedisApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootMongodbRedisApplication.class, args);
    }

}
