package com.example.cloudlab;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class KillController {

    @RequestMapping("/kill")
    public void kill(){
        System.exit(1);
    }
}