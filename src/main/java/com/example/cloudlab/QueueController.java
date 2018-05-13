package com.example.cloudlab;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class QueueController {

    @Autowired
    private AmqpTemplate amqpTemplate;

    @RequestMapping("/sendmessage")
    public void sendMessage(String input){
        amqpTemplate.convertAndSend("myQueue",input);
    }

    @RequestMapping("/getmessage")
    public String getMessage(){
        Message message = amqpTemplate.receive("myQueue");
        if(message == null)
            return "Queue empty";

        return message.toString();
    }
}
