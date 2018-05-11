package com.example.cloudlab;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloWorldController {

    @Value("${helloworld.message:'Hola Mundo - default!'}")
    private String helloMessage;

    @RequestMapping("hello")
    public String helloWorld(){
        return helloMessage;
    }
}