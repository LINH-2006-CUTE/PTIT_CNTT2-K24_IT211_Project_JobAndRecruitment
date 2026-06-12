package com.example.jobandrecruitment.advice;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.AfterThrowing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import java.util.Arrays;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    // Đo thời gian chạy và log toàn bộ các hàm xử lý logic tại tầng Service Impl
    @Around("execution(* com.example.jobandrecruitment.service.impl..*(..))")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        String args = Arrays.toString(joinPoint.getArgs());

        logger.info(">>> [AOP START] Executing: {}.{}() with arguments: {}", className, methodName, args);

        Object result;
        try {
            result = joinPoint.proceed();
        } catch (Throwable throwable) {
            logger.error("!!! [AOP ERROR] Exception in {}.{}(): {}", className, methodName, throwable.getMessage());
            throw throwable;
        }

        long duration = System.currentTimeMillis() - startTime;
        logger.info("<<< [AOP END] Finished: {}.{}() - Executed in {} ms", className, methodName, duration);

        return result;
    }

    // Bắt riêng các Exception quăng ra từ Controller hoặc Service để giám sát hệ thống
    @AfterThrowing(pointcut = "execution(* com.example.jobandrecruitment..*(..))", throwing = "exception")
    public void logAfterThrowing(Object exception) {
        if (exception instanceof Exception) {
            logger.warn("⚠️ [AOP EXCEPTION SYSTEM]: Captured error detail -> {}", ((Exception) exception).getMessage());
        }
    }
}