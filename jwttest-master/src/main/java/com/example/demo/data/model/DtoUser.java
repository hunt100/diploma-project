package com.example.demo.data.model;

import com.example.demo.data.entity.Authority;
import lombok.Data;

import java.util.List;

@Data
public class DtoUser extends BaseForm{
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private String patronymic;
    private String birthDate;
    private String telephone;
    private String iin;
    private String email;
    private String role;
    private List<Authority> roles;
    private boolean isBanned;
    private String initialsAvatar;
}
