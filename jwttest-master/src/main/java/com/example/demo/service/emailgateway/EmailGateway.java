package com.example.demo.service.emailgateway;

public interface EmailGateway {

    void sendMessage(String to, String subject, String text) ;
}
