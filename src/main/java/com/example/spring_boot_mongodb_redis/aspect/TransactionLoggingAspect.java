package com.example.spring_boot_mongodb_redis.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Aspect
@Component
public class TransactionLoggingAspect {

    @Around("execution(* com.example.spring_boot_mongodb_redis.service..*(..))")
    public Object logRedisTransaction(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        String method = methodSignature.getDeclaringType().getSimpleName() + "." + methodSignature.getName();

        Object[] args = joinPoint.getArgs();

        log.info("Redis Cache Operation Start: {} with args: {}", method, args);
        try {
            long start = System.currentTimeMillis();
            Object result = joinPoint.proceed();
            long end = System.currentTimeMillis();

            log.info("Redis Cache Operation End: {} - execution time: {} ms, cache hit/miss info: Cache operation completed", method, (end - start));
            return result;
        } catch (Throwable ex) {
            if (ex instanceof ResponseStatusException rse) {
                log.warn("Redis Cache Operation Error in {}: {} {}", method, rse.getStatusCode(), rse.getReason());
            } else {
                log.error("Redis Cache Operation Error in {}: {} {}", method, ex.getClass().getSimpleName(), ex.getMessage());
            }
            throw ex;
        }
    }
}
