package com.example.demo2;

import com.example.demo2.aop.TcbFeatureFlag;
import org.springframework.stereotype.Service;

@Service("MyService1Impl")
public class MyService1Impl implements MyService {

    public String newFeature() {
        return "MyService1Impl New Feature is executed!";
    }

    @TcbFeatureFlag(value = "myFeature", newFeature = "newFeature")
    @Override
    public String executeFeature() {
        return "MyService1Impl Old Feature is executed!";
    }
}