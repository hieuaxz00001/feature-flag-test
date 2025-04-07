package com.example.demo2.aop;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface TcbFeatureFlag {
    String value(); // Tên cờ tính năng
    String newFeature() default ""; // Tên phương thức A// version sẽ removed
}