package com.example.demo;

import io.micrometer.core.instrument.Metrics;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloWorldController {

    @Value("${helloworld.message:'Helloworld - default!'}")
    private String helloMessage;

    @RequestMapping("hello")
    public String helloWorld(){

        Metrics.counter("application.helloworld.hit").increment();

        return helloMessage;
    }
}
