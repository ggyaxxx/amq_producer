package com.example.demo;


import jakarta.annotation.PostConstruct;
import jakarta.jms.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DebugCF {

    @Qualifier("jmsConnectionFactory")
    @Autowired(required = false)
    ConnectionFactory cf;

    @PostConstruct
    public void test() {
        System.out.println(">>> CONNECTION FACTORY = " + cf);
    }
}
