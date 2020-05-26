package com.example.demo.service.validator;

import com.example.demo.data.model.DtoUser;

import java.util.Map;

public interface UserValidator {

    boolean isUserIin(String firstName, String lastName, String patronymic, String iin);

    boolean isEmailValid(String email);

    boolean isPhoneValid (String telephone);

    Map<String, String> checkValidation (DtoUser user);
}
