package com.example.demo2;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final MyService1 myService1;

    public HomeController(MyService1 myService1) {
        this.myService1 = myService1;
    }

    @GetMapping("/test")
    public String homepage() {
        return myService1.executeFeature();  // Trả về trang index.html
    }

}
