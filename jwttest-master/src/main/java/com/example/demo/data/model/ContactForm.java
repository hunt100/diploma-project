package com.example.demo.data.model;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
public class ContactForm extends BaseForm {
    @NotBlank(message = "Имя не должно быть пустым")
    private String name;
    @Email(message = "Ошибка в валидации email почты")
    @NotBlank(message = "Email не должно быть пустым")
    private String email;
    private String text;
}
