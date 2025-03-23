package com.example.demo2;

import org.apache.coyote.BadRequestException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.ff4j.FF4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Aspect
@Component

public class FeatureFlagAspect {
    private static final Logger log = LoggerFactory.getLogger(FeatureFlagAspect.class);

    @Value("${app.version}")
    private String appVersion;

    @Autowired
    private FF4j ff4j;

    @Around("@annotation(checkFeatureFlag)")
    public Object checkFeatureFlag(ProceedingJoinPoint joinPoint, CheckFeatureFlag checkFeatureFlag) throws Throwable {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        Class<?> targetClass = joinPoint.getTarget().getClass();

        var feature = ff4j.getFeature(checkFeatureFlag.value());
        var customProperties = feature.getCustomProperties();
        var version = customProperties.get(CustomPropertiesEnum.releasedFromVersion.name()).asString();

        log.info("version: {} - appVersion: {}", version, appVersion);
        if (Utils.compareVersion(version, appVersion)) {
            log.info("Process with old version");
            return joinPoint.proceed();
        }

        // Kiểm tra xem lớp có annotation @Controller hoặc @Service không
        if (targetClass.isAnnotationPresent(org.springframework.web.bind.annotation.RestController.class)) {
            log.info("Method {} is in a Controller: {}", joinPoint.getSignature().getName(), className);
            return checkFeatureFlagInController(joinPoint, checkFeatureFlag);
        } else if (targetClass.isAnnotationPresent(org.springframework.stereotype.Service.class)) {
            log.info("Method {} is in a Service: {}", joinPoint.getSignature().getName(), className);
            return checkFlagInService(joinPoint, checkFeatureFlag);
        } else {
            log.info("Not support feature flag method {} in class: {}", joinPoint.getSignature().getName(), className);
            throw new BadRequestException("Not support feature flag method " + joinPoint.getSignature().getName() + "in class: " + className);
        }
    }

    private Object checkFeatureFlagInController(ProceedingJoinPoint joinPoint, CheckFeatureFlag checkFeatureFlag) throws Throwable {
        String featureName = checkFeatureFlag.value();
        String newFeature = checkFeatureFlag.newFeature();
        if (ff4j.check(featureName)) {
            return processNewFeature(joinPoint, newFeature);
        } else {
            log.info("Feature {} is not enabled", featureName);
            throw new RuntimeException("Feature " + featureName + " is not enabled");
        }
    }

    private Object checkFlagInService(ProceedingJoinPoint joinPoint, CheckFeatureFlag checkFeatureFlag) throws Throwable {
        String featureName = checkFeatureFlag.value();
        String newFeature = checkFeatureFlag.newFeature();
        if (ff4j.check(featureName)) {
            return processNewFeature(joinPoint, newFeature);
        } else {
            return joinPoint.proceed();
        }
    }

    private Object processNewFeature(ProceedingJoinPoint joinPoint, String newFeature) throws Throwable {
        if (newFeature.isEmpty()) {
            return joinPoint.proceed();
        } else {
            log.info("Proccessed new feature: {}", newFeature);
            return invokeMethod(joinPoint.getTarget(), newFeature);
        }
    }

    private Object invokeMethod(Object target, String methodName) throws Exception {
        Method method = target.getClass().getMethod(methodName);
        return method.invoke(target);
    }
}