package com.example.demo2;

import org.springframework.stereotype.Service;

@Service("MyService1Impl")
public class MyService1Impl implements MyService {

    public String newFeature() {
        return "MyService1Impl New Feature is executed!";
    }

    @CheckFeatureFlag(value = "myFeature", newFeature = "newFeature")
    @Override
    public String executeFeature() {
        return "MyService1Impl Old Feature is executed!";
    }
}