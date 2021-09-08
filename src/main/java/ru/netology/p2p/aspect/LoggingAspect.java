package ru.netology.p2p.aspect;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
public class LoggingAspect {
    //    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final Logger logger = LogManager.getLogger(LoggingAspect.class);

    @Pointcut("within(@org.springframework.stereotype.Repository *)" +
            " || within(@org.springframework.stereotype.Service *)" +
            " || within(@org.springframework.web.bind.annotation.RestController *)")
    public void springBeanPointcut() { }

    @AfterThrowing(pointcut = "springBeanPointcut()", throwing = "e")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable e) {
        logger.error("Exception in " + joinPoint.getSignature().getDeclaringTypeName() + "." +
                joinPoint.getSignature().getName() + "() with message = " + e.getMessage());
    }

    @Around("springBeanPointcut()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable{
        if(logger.isInfoEnabled()) {
            infoLog(joinPoint);
        } else if (logger.isDebugEnabled()) {
            debugLog(joinPoint);
        }
        try {
            Object result = joinPoint.proceed();
            infoOrDebugLogResultWorkMethod(joinPoint, result);
            return result;
        } catch (IllegalArgumentException e) {
            innerExceptionLog(joinPoint);
            throw e;
        }
    }

    private void infoLog(ProceedingJoinPoint joinPoint) {
        logger.info(joinPoint.getKind() + "::" +
                joinPoint.getSignature().getName() + "() with argument[s] = " +
                Arrays.toString(joinPoint.getArgs()));
    }

    private void debugLog(ProceedingJoinPoint joinPoint) {
        logger.debug("Enter: " + joinPoint.getSignature().getDeclaringTypeName() +
                "." + joinPoint.getSignature().getName() + "() with argument[s] = " +
                Arrays.toString(joinPoint.getArgs()));
    }

    private void innerExceptionLog(ProceedingJoinPoint joinPoint) {
        logger.error("Illegal argument: " + Arrays.toString(joinPoint.getArgs()) +
                " in " + joinPoint.getSignature().getDeclaringTypeName() + "." +
                joinPoint.getSignature().getName() + "()");
    }

    private void infoOrDebugLogResultWorkMethod(ProceedingJoinPoint joinPoint, Object result) {
        if(logger.isInfoEnabled() && result != null) {
            logger.info(joinPoint.getKind() +
                    ":" + joinPoint.getSignature().getName() + "() with result = " +
                    result);
        } else if (logger.isDebugEnabled()) {
            logger.debug("Enter: " + joinPoint.getSignature().getDeclaringTypeName() +
                    "." + joinPoint.getSignature().getName() + "() with result = " +
                    result);
        }
    }
}
