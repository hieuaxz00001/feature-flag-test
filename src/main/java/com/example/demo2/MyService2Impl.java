package com.example.demo2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service("MyService2Impl")
public class MyService2Impl implements MyService{

    private static final Logger log = LoggerFactory.getLogger(MyService2Impl.class);

    public String featureA() {
        return "MyService2Impl New Feature is executed!";
    }

    public String featureB() {
        return "MyService2Impl Old Feature is executed!";
    }

    @Override
    public String executeFeature() {
        String rs = "";
        getRS(rs);
        return rs;
    }

    @CheckFeatureFlag(value = "myFeature", newFeature = "featureA")
    private void getRS (String rs){
        rs = featureB();
        log.info(rs);
    }
}
