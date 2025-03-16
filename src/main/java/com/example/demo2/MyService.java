package com.example.demo2;

import org.springframework.stereotype.Service;

@Service
public class MyService implements MyService1{

    public String featureA() {
        return "Feature A is executed!";
    }

    public String featureB() {
        return "Feature B is executed!";
    }

    @CheckFeatureFlag(value = "myFeature", newFeature = "featureA")
    @Override
    public String executeFeature() {
        return "Feature B is executed!";
    }
}