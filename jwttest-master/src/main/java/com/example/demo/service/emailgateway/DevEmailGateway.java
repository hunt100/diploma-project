package com.example.demo.service.emailgateway;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("temp")
@Slf4j
public class DevEmailGateway implements EmailGateway {

    @Override
    public void sendMessage(String to, String subject, String text) {
        log.info("Send message to {}, subject: {} with text: {}", to, subject, text);
    }
}
