package com.example.demo2;

import com.example.demo2.aop.TcbFeatureFlag;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    private final MyService myService1;
    private final MyService myService2;
    private final MyService myService3;


    public HomeController(@Qualifier("MyService2Impl") MyService myService2, @Qualifier("MyService1Impl") MyService myService1, @Qualifier("MyService3Impl") MyService myService3) {
        this.myService1 = myService1;
        this.myService2 = myService2;
        this.myService3 = myService3;
    }

    @GetMapping("/test")
    public String homepage() {
        return myService1.executeFeature();
    }

    @GetMapping("/test1")
    public String homepage2() {
        return myService2.executeFeature();
    }

    @GetMapping("/test2")
    @TcbFeatureFlag(value = "myFeature", newFeature = "homepage")
    public String homepage3() {
        return myService2.executeFeature();
    }

    @GetMapping("/test5")
    public String homepage4() {
        return myService3.executeFeature();
    }

}
