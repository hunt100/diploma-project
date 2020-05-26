package com.example.demo.data.model;

import lombok.Data;

@Data
public class PasswordResetForm {
    private String login;
    private String password;
    private String confirmPassword;
}
