package com.niu.core.config.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;


public class MethodCallStackAspect {

    @Aspect
    @Component
    @Slf4j
    public static class ControllerCall {

        @Pointcut("execution(* com..*.controller..*.*(..))")
        public void controllerMethods() {
        }

        @Around("controllerMethods()")
        public Object aroundMethod(ProceedingJoinPoint joinPoint) throws Throwable {
            String className = joinPoint.getTarget().getClass().getName();
            String methodName = joinPoint.getSignature().getName();
            log.debug(className + " -> " + methodName + "() begin -> ");
            Object result = joinPoint.proceed();
            log.debug(className + " -> " + methodName + "() ended result => " + result);
            return result;
        }

    }

    @Aspect
    @Component
    @Slf4j
    public static class ServiceCall {

        @Pointcut("execution(* com..*.service..*.*(..))")
        public void serviceMethods() {
        }

        @Around("serviceMethods()")
        public Object aroundMethod(ProceedingJoinPoint joinPoint) throws Throwable {
            String className = joinPoint.getTarget().getClass().getName();
            String methodName = joinPoint.getSignature().getName();
            log.debug(className + " -> " + methodName + "() begin -> ");
            Object result = joinPoint.proceed();
            log.debug(className + " -> " + methodName + "() ended result => " + result);
            return result;
        }
    }

    @Aspect
    @Component
    @Slf4j
    public static class MapperCall {

        @Pointcut("execution(* com..*.mapper..*.*(..))")
        public void mapperMethods() {
        }

        @Around("mapperMethods()")
        public Object aroundMethod(ProceedingJoinPoint joinPoint) throws Throwable {
            String className = joinPoint.getTarget().getClass().getName();
            String methodName = joinPoint.getSignature().getName();
            log.debug(className + " -> " + methodName + "() begin => ");
            Object result = joinPoint.proceed();
            log.debug(className + " -> " + methodName + "() ended result => " + result);
            return result;
        }

    }

}
