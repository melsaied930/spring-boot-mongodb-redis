package com.example.spring_boot_mongodb_redis.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
public class TransactionLoggingAspect {

    private static final Logger mongoLogger = LoggerFactory.getLogger("MongoDBTransactions");
    private static final Logger redisLogger = LoggerFactory.getLogger("RedisTransactions");
    private static final Logger serviceLogger = LoggerFactory.getLogger("ServiceOperations");

    // Pointcut for MongoDB repository methods
    @Pointcut("execution(* com.example.spring_boot_mongodb_redis.repository.*.*(..)))")
    public void mongoRepositoryMethods() {
    }

    // Pointcut for Redis cache operations
    @Pointcut("@annotation(org.springframework.cache.annotation.Cacheable) || " +
            "@annotation(org.springframework.cache.annotation.CachePut) || " +
            "@annotation(org.springframework.cache.annotation.CacheEvict)")
    public void cacheOperations() {
    }

    // Pointcut for service methods
    @Pointcut("execution(* com.example.spring_boot_mongodb_redis.service.*.*(..))")
    public void serviceMethods() {
    }

    @Around("mongoRepositoryMethods()")
    public Object logMongoDBTransaction(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getSignature().getDeclaringTypeName();
        Object[] args = joinPoint.getArgs();

        long startTime = System.currentTimeMillis();
        mongoLogger.info("MongoDB Transaction Start: {}.{} with args: {}",
                className, methodName, Arrays.toString(args));

        try {
            Object result = joinPoint.proceed();
            long endTime = System.currentTimeMillis();

            mongoLogger.info("MongoDB Transaction End: {}.{} - execution time: {} ms, result: {}",
                    className, methodName, (endTime - startTime), result);

            return result;
        } catch (Exception e) {
            mongoLogger.error("MongoDB Transaction Error in {}.{}: {}",
                    className, methodName, e.getMessage(), e);
            throw e;
        }
    }

    @Around("cacheOperations() && serviceMethods()")
    public Object logRedisTransaction(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getSignature().getDeclaringTypeName();
        Object[] args = joinPoint.getArgs();

        long startTime = System.currentTimeMillis();
        redisLogger.info("Redis Cache Operation Start: {}.{} with args: {}",
                className, methodName, Arrays.toString(args));

        try {
            Object result = joinPoint.proceed();
            long endTime = System.currentTimeMillis();

            redisLogger.info("Redis Cache Operation End: {}.{} - execution time: {} ms, cache hit/miss info: {}",
                    className, methodName, (endTime - startTime),
                    result != null ? "Cache operation completed" : "Cache operation null result");

            return result;
        } catch (Exception e) {
            redisLogger.error("Redis Cache Operation Error in {}.{}: {}",
                    className, methodName, e.getMessage(), e);
            throw e;
        }
    }

    @Around("serviceMethods() && !cacheOperations()")
    public Object logServiceOperation(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getSignature().getDeclaringTypeName();
        Object[] args = joinPoint.getArgs();

        long startTime = System.currentTimeMillis();
        serviceLogger.info("Service Operation Start: {}.{} with args: {}",
                className, methodName, Arrays.toString(args));

        try {
            Object result = joinPoint.proceed();
            long endTime = System.currentTimeMillis();

            serviceLogger.info("Service Operation End: {}.{} - execution time: {} ms",
                    className, methodName, (endTime - startTime));

            return result;
        } catch (Exception e) {
            serviceLogger.error("Service Operation Error in {}.{}: {}",
                    className, methodName, e.getMessage(), e);
            throw e;
        }
    }
}