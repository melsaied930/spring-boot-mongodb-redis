package com.example.spring_boot_mongodb_redis.service;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class CacheMetrics {

    private final Map<String, AtomicInteger> cacheHits = new ConcurrentHashMap<>();
    private final Map<String, AtomicInteger> cacheMisses = new ConcurrentHashMap<>();
    private final Map<String, AtomicInteger> cacheOperations = new ConcurrentHashMap<>();

    public void recordCacheHit(String cacheName) {
        cacheHits.computeIfAbsent(cacheName, k -> new AtomicInteger(0)).incrementAndGet();
        recordCacheOperation(cacheName);
    }

    public void recordCacheMiss(String cacheName) {
        cacheMisses.computeIfAbsent(cacheName, k -> new AtomicInteger(0)).incrementAndGet();
        recordCacheOperation(cacheName);
    }

    private void recordCacheOperation(String cacheName) {
        cacheOperations.computeIfAbsent(cacheName, k -> new AtomicInteger(0)).incrementAndGet();
    }

    public int getCacheHits(String cacheName) {
        return cacheHits.getOrDefault(cacheName, new AtomicInteger(0)).get();
    }

    public int getCacheMisses(String cacheName) {
        return cacheMisses.getOrDefault(cacheName, new AtomicInteger(0)).get();
    }

    public int getTotalOperations(String cacheName) {
        return cacheOperations.getOrDefault(cacheName, new AtomicInteger(0)).get();
    }

    public double getHitRatio(String cacheName) {
        int hits = getCacheHits(cacheName);
        int total = getTotalOperations(cacheName);
        return total > 0 ? (double) hits / total : 0.0;
    }

    public void resetMetrics() {
        cacheHits.clear();
        cacheMisses.clear();
        cacheOperations.clear();
    }

    public Map<String, Object> getMetrics() {
        Map<String, Object> metrics = new ConcurrentHashMap<>();

        cacheOperations.keySet().forEach(cacheName -> {
            Map<String, Object> cacheMetrics = new ConcurrentHashMap<>();
            cacheMetrics.put("hits", getCacheHits(cacheName));
            cacheMetrics.put("misses", getCacheMisses(cacheName));
            cacheMetrics.put("total", getTotalOperations(cacheName));
            cacheMetrics.put("hitRatio", String.format("%.2f", getHitRatio(cacheName) * 100) + "%");

            metrics.put(cacheName, cacheMetrics);
        });

        return metrics;
    }
}