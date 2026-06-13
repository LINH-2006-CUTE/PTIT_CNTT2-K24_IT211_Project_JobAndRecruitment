package com.example.jobandrecruitment.advice;

import com.example.jobandrecruitment.model.dto.response.JobApplicationResponse;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
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
    @AfterReturning(
            pointcut = "execution(* com.example.jobandrecruitment.service.impl.JobApplicationServiceImpl.applyJob(..))",
            returning = "result"
    )
    public void logJobApplicationSuccess(JoinPoint joinPoint, JobApplicationResponse result) {
        logger.info("[AOP AFTER_RETURNING] Candidate ID: {} applied for Job ID: {}",
                result.getCandidateId(), result.getJobId());
    }
    @AfterThrowing(
            pointcut =
                    "execution(* com.example.jobandrecruitment.service.impl..*(..))",
            throwing = "exception"
    )
    public void logAfterThrowing(Exception exception)
    {
        logger.warn(
                "Captured error -> {}",
                exception.getMessage()
        );
    }
}