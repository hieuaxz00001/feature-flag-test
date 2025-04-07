package com.example.demo2.aop;

import com.example.demo2.constant.CustomPropertiesEnum;
import com.example.demo2.utils.Utils;
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
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.util.Optional;

@Aspect
@Component
public class FeatureFlagAspect {
    private static final Logger log = LoggerFactory.getLogger(FeatureFlagAspect.class);

    @Value("${app.version}")
    private String microserviceVersion;

    @Autowired
    private FF4j ff4j;

    @Around("@annotation(tcbFeatureFlag)")
    public Object checkFeatureFlag(ProceedingJoinPoint joinPoint, TcbFeatureFlag tcbFeatureFlag) throws Throwable {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        Class<?> targetClass = joinPoint.getTarget().getClass();

        var feature = ff4j.getFeature(tcbFeatureFlag.value());
        var customProperties = feature.getCustomProperties();
        var version = customProperties.get(CustomPropertiesEnum.releasedFromVersion.name()).asString();
        var featureFlagAppVersion = customProperties.get(CustomPropertiesEnum.applyFromAppVersion.name()).asString();
        var appVersion = getAppVersion();

        log.info("version: {} - microserviceVersion: {}", version, microserviceVersion);
        if (Utils.compareVersion(version, microserviceVersion)) {
            log.info("You using old microserviceVersion. Process with old version");
            return joinPoint.proceed();
        }

        log.info("version: {} - featureFlagAppVersion: {}", appVersion, featureFlagAppVersion);
        if (Utils.compareVersion(featureFlagAppVersion, appVersion)){
            log.info("You using old appVersion. Process with old version");
            return joinPoint.proceed();
        }

        if (targetClass.isAnnotationPresent(org.springframework.web.bind.annotation.RestController.class)
                || targetClass.isAnnotationPresent(org.springframework.web.bind.annotation.RequestMapping.class)
        ) {
            log.info("Method {} is in a Controller: {}", joinPoint.getSignature().getName(), className);
            return checkFeatureFlagInController(joinPoint, tcbFeatureFlag);
        } else if (targetClass.isAnnotationPresent(org.springframework.stereotype.Service.class)) {
            log.info("Method {} is in a Service: {}", joinPoint.getSignature().getName(), className);
            return checkFlagInService(joinPoint, tcbFeatureFlag);
        } else {
            log.info("Not support feature flag method {} in class: {}", joinPoint.getSignature().getName(), className);
            throw new BadRequestException("Not support feature flag method " + joinPoint.getSignature().getName() + "in class: " + className);
        }
    }

    private Object checkFeatureFlagInController(ProceedingJoinPoint joinPoint, TcbFeatureFlag tcbFeatureFlag) throws Throwable {
        String featureName = tcbFeatureFlag.value();
        String newFeature = tcbFeatureFlag.newFeature();
        if (ff4j.check(featureName)) {
            return processNewFeature(joinPoint, newFeature);
        } else {
            log.info("Feature {} is not enabled", featureName);
            throw new RuntimeException("Feature " + featureName + " is not enabled");
        }
    }

    private Object checkFlagInService(ProceedingJoinPoint joinPoint, TcbFeatureFlag tcbFeatureFlag) throws Throwable {
        String featureName = tcbFeatureFlag.value();
        String newFeature = tcbFeatureFlag.newFeature();
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
            log.info("Processed new feature: {}", newFeature);
            return invokeMethod(joinPoint.getTarget(), newFeature);
        }
    }

    private Object invokeMethod(Object target, String methodName) throws Exception {
        Method method = target.getClass().getMethod(methodName);
        return method.invoke(target);
    }

    private String getAppVersion(){
        return Optional.ofNullable(RequestContextHolder.getRequestAttributes())
                .map(ServletRequestAttributes.class::cast)
                .map(ServletRequestAttributes::getRequest)
                .map(servletRequest -> servletRequest.getHeader("Tcb-User-Agent"))
                .orElse("0.0.0");
    }
}