package com.example.demo;

import io.micrometer.core.instrument.Metrics;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CacheExampleController {

    @Cacheable("uppercase")
    @RequestMapping("/uppercase")
    public String uppercase(String input ){

        Metrics.counter("application.cache.miss").increment();

        try {Thread.sleep(5000); } catch (InterruptedException e) {}

        return input.toUpperCase();
    }
}