package com.example.spring_boot_mongodb_redis.controller;

import com.example.spring_boot_mongodb_redis.service.CacheMetrics;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/api/cache")
public class CacheController {

    private final CacheMetrics cacheMetrics;
    private final CacheManager cacheManager;

    @Value("${cache.enabled:true}")
    private boolean cacheEnabled;

    public CacheController(CacheMetrics cacheMetrics, CacheManager cacheManager) {
        this.cacheMetrics = cacheMetrics;
        this.cacheManager = cacheManager;
    }

    @GetMapping("/metrics")
    public Map<String, Object> getCacheMetrics() {
        Map<String, Object> response = new HashMap<>();
        response.put("cacheImplementation", cacheManager.getClass().getSimpleName());
        response.put("cacheEnabled", cacheEnabled);
        response.put("metrics", cacheMetrics.getMetrics());
        return response;
    }

    @GetMapping("/status")
    public Map<String, Object> getCacheStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("cacheEnabled", cacheEnabled);
        status.put("cacheImplementation", cacheManager.getClass().getSimpleName());
        status.put("availableCaches", cacheManager.getCacheNames());
        return status;
    }

    @PostMapping("/clear")
    public Map<String, String> clearCache() {
        cacheManager.getCacheNames().forEach(cacheName -> Objects
                .requireNonNull(cacheManager
                        .getCache(cacheName))
                .clear());
        cacheMetrics.resetMetrics();

        Map<String, String> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "All caches cleared and metrics reset");
        return response;
    }
}

//curl -X GET http://localhost:8080/api/cache/metrics | jq
//curl -X GET http://localhost:8080/api/cache/status | jq
//curl -X POST http://localhost:8080/api/cache/clear | jq
