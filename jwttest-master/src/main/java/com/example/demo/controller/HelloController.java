package com.example.demo.controller;

import com.example.demo.data.model.ContactForm;
import com.example.demo.service.emailgateway.EmailGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping(value = "/v1/api/contact")
public class HelloController {
    private final EmailGateway emailGateway;

    @Value("${spring.mail.username}")
    private String emailTo;

    @Autowired
    public HelloController(EmailGateway emailGateway) {
        this.emailGateway = emailGateway;
    }

    @PostMapping
    public ResponseEntity<?> sendContactInfo(@RequestBody @Valid ContactForm contactForm, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().build();
        }
        emailGateway.sendMessage(emailTo, "Контактная форма для администрации от:" + contactForm.getEmail(),
                "Имя пользователя:" + contactForm.getName() + ".\n" + contactForm.getText());
        return ResponseEntity.ok().build();
    }

}
