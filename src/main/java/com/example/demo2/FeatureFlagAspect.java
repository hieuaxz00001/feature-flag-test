package com.example.demo2;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.ff4j.FF4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Aspect
@Component
public class FeatureFlagAspect {

    @Autowired
    private FF4j ff4j;

    @Around("@annotation(checkFeatureFlag)")
    public Object checkFeatureFlag(ProceedingJoinPoint joinPoint, CheckFeatureFlag checkFeatureFlag) throws Throwable {
        String featureName = checkFeatureFlag.value();
        String methodA = checkFeatureFlag.newFeature();

        if (ff4j.check(featureName)) {
            return invokeMethod(joinPoint.getTarget(), methodA);
        } else {
            return joinPoint.proceed();
        }
    }

    private Object invokeMethod(Object target, String methodName) throws Exception {
        Method method = target.getClass().getMethod(methodName);
        return method.invoke(target);
    }
}