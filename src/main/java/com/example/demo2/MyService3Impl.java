package com.example.demo2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service("MyService3Impl")
public class MyService3Impl implements MyService{
    private static final Logger log = LoggerFactory.getLogger(MyService3Impl.class);


    public void featureA(){
        log.info("MyService3Impl New Feature is executed!");
    }

    @CheckFeatureFlag(value = "myFeature", newFeature = "featureA")
    public void featureB() {
        log.info("MyService3Impl Old Feature is executed!");
    }

    @Override
    public String executeFeature() {
        featureB();
        return "Done";
    }
}
