package com.example.demo.service.validator;

import com.example.demo.data.entity.DaoUser;
import com.example.demo.data.entity.UserProfile;
import com.example.demo.data.model.DtoUser;
import com.example.demo.service.JwtUserDetailsService;
import com.example.demo.service.UserProfileService;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;


@Component
@Profile("dev")
public class DevUserValidator implements UserValidator {
    private final JwtUserDetailsService userDetailsService;
    private final UserProfileService userProfileService;

    private static final String BIRTH_DATE_PATTERN = "\\d{4}-\\d{2}-\\d{2}";

    @Autowired
    public DevUserValidator(JwtUserDetailsService userDetailsService, UserProfileService userProfileService) {
        this.userDetailsService = userDetailsService;
        this.userProfileService = userProfileService;
    }

    @Override
    public boolean isUserIin(String firstName, String lastName, String patronymic, String iin) {
        return true;
    }

    @Override
    public boolean isEmailValid(String email) {
        return EmailValidator.getInstance().isValid(email.trim());
    }

    @Override
    public boolean isPhoneValid(String telephone) {
        return !telephone.trim().isEmpty() && telephone.length() == 11;
    }

    @Override
    public Map<String, String> checkValidation(DtoUser user) {
        DaoUser daoUser = userDetailsService.findUserByUsername(user.getUsername());
        UserProfile userProfile = userProfileService.findByEmail(user.getEmail());
        Map<String, String> errorMap = new HashMap<>();
        if (daoUser != null) {
            errorMap.put(DtoUser.class.getSimpleName() + ".username", "user already exist");
        }
        if (userProfile != null) {
            errorMap.put(DtoUser.class.getSimpleName() + ".email", "user already exist");
        }
        if (!isPhoneValid(user.getTelephone())) {
            errorMap.put(DtoUser.class.getSimpleName() + ".telephone", "telephone is not valid");
        }
        if (!isEmailValid(user.getEmail())) {
            errorMap.put(DtoUser.class.getSimpleName() + ".email", "email is not valid");
        }
        if (!isUserIin(user.getFirstName(), user.getLastName(), user.getPatronymic(), user.getIin())) {
            errorMap.put(DtoUser.class.getSimpleName() + ".iin", "iin is not confirmed for this FIO");
        }
        if (user.getFirstName() == null || user.getFirstName().trim().isEmpty()) {
            errorMap.put(DtoUser.class.getSimpleName() + ".firstName", "firstName can not be empty");
        }
        if (user.getLastName() == null || user.getFirstName().trim().isEmpty()) {
            errorMap.put(DtoUser.class.getSimpleName() + ".lastName", "lastName can not be empty");
        }
        if (user.getBirthDate() == null) {
            errorMap.put(DtoUser.class.getSimpleName() + ".birthDate", "birthDate can not be empty");
        } else if (!user.getBirthDate().matches(BIRTH_DATE_PATTERN)) {
            errorMap.put(DtoUser.class.getSimpleName() + ".birthDate", "wrong pattern for birthDate");
        }
        return errorMap;
    }
}
