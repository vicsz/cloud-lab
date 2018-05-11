package com.example.cloudlab;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CacheExampleController {

    @Cacheable("uppercase")
    @RequestMapping("/uppercase")
    public String uppercase(String input ){
        try {Thread.sleep(5000); } catch (InterruptedException e) {}

        return input.toUpperCase();
    }
}