package com.example.spring_boot_mongodb_redis.interceptor;

import com.example.spring_boot_mongodb_redis.service.CacheMetrics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.interceptor.CacheInterceptor;
import org.springframework.cache.interceptor.CacheOperationInvoker;
import org.springframework.cache.interceptor.CacheOperationSource;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Component
@Primary
public class MetricsRecordingCacheInterceptor extends CacheInterceptor {

    private static final Logger redisLogger = LoggerFactory.getLogger("RedisTransactions");
    private final CacheMetrics metricsCollector;

    public MetricsRecordingCacheInterceptor(CacheMetrics metricsCollector, CacheOperationSource cacheOperationSource) {
        this.metricsCollector = metricsCollector;
        setCacheOperationSource(cacheOperationSource);
    }

    @Override
    protected Object execute(CacheOperationInvoker invoker, Object target, Method method, Object[] args) {
        String className = target.getClass().getSimpleName();
        String methodName = method.getName();

        redisLogger.debug("Cache interceptor executing: {}.{}", className, methodName);

        long startTime = System.currentTimeMillis();
        Object result = super.execute(invoker, target, method, args);
        long endTime = System.currentTimeMillis();

        // Try to determine if this was a cache hit or miss
        // This is a simplification - in reality, it's more complex to determine if a cache hit occurred
        if (result != null) {
            // Assume cache hit for simplicity
            metricsCollector.recordCacheHit("users");
            redisLogger.debug("Cache operation on {}.{} completed in {} ms (likely HIT)",
                    className, methodName, (endTime - startTime));
        } else {
            // Assume cache miss
            metricsCollector.recordCacheMiss("users");
            redisLogger.debug("Cache operation on {}.{} completed in {} ms (likely MISS)",
                    className, methodName, (endTime - startTime));
        }

        return result;
    }

    @Override
    public void afterPropertiesSet() {
        super.afterPropertiesSet();
        redisLogger.info("MetricsRecordingCacheInterceptor initialized");
    }
}