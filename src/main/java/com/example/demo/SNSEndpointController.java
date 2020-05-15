package com.example.demo;

import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.model.SubscribeResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;

@Slf4j
@RestController
@RequestMapping("/topic-subscriber")
public class SNSEndpointController {

    @Autowired
    private AmazonSNS sns;

    @PostConstruct
    public void init() {
        System.err.println("Trying to susbcribe!");
        SubscribeResult subscribeResult = sns.subscribe("arn:aws:sns:us-east-1:000000000000:foo", "http", "http://localhost:6969/foo");
        System.err.println("Arn: " + subscribeResult.getSubscriptionArn());
    }

}
