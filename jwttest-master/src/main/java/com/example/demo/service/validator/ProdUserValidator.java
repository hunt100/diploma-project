package com.example.demo.service.validator;

import com.example.demo.data.entity.DaoUser;
import com.example.demo.data.entity.UserProfile;
import com.example.demo.data.model.DtoUser;
import com.example.demo.data.model.KazPostUserResponse;
import com.example.demo.service.JwtUserDetailsService;
import com.example.demo.service.UserProfileService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Profile("prod")
@Slf4j
public class ProdUserValidator implements UserValidator {
    private final JwtUserDetailsService userDetailsService;
    private final UserProfileService userProfileService;

    @Value("${validation.telephone.prefix}")
    private List<String> phonePrefix;

    private static final String BIRTH_DATE_PATTERN = "\\d{4}-\\d{2}-\\d{2}";

    @Autowired
    public ProdUserValidator(JwtUserDetailsService userDetailsService, UserProfileService userProfileService) {
        this.userDetailsService = userDetailsService;
        this.userProfileService = userProfileService;
    }

    @Override
    public boolean isUserIin(String firstName, String lastName, String patronymic, String iin) {
        String url = "https://post.kz/mail-app/api/public/transfer/loadName/";
        HttpRequestBase request = new HttpGet(url + iin);
        request.setHeader("Host", "post.kz");
        try {
            log.info("GET Request to KazPost: {}", request.getURI().toString());
            CloseableHttpResponse response = HttpClientBuilder.create().build().execute(request);
            String result = IOUtils.toString(response.getEntity().getContent());
            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                log.error("KazPost return error, problems with request? Response Status: {}; Response body: {}", response.getStatusLine().getStatusCode(), result);
                return false;
            }
            if (result == null || result.trim().equals("")) {
                log.error("KazPost return 200 status, bit the body was empty");
                return false;
            }
            KazPostUserResponse userResponse = new ObjectMapper().readValue(result, KazPostUserResponse.class);
            List<String> userNameData = userResponse.divideFullNameBySpaces();
            switch (userNameData.size()) {
                case 2:
                    return userNameData.get(0).equalsIgnoreCase(lastName) && userNameData.get(1).equalsIgnoreCase(firstName);
                case 3:
                    return userNameData.get(0).equalsIgnoreCase(lastName) && userNameData.get(1).equalsIgnoreCase(firstName) && userNameData.get(2).equalsIgnoreCase(patronymic);
                default: return false;
            }
        } catch (Exception e) {
            log.error("Something go wrong: Exception {}", e.toString());
            return false;
        }
    }

    @Override
    public boolean isEmailValid(String email) {
        return EmailValidator.getInstance().isValid(email.trim());
    }

    @Override
    public boolean isPhoneValid(String telephone) {
        if (telephone.trim().isEmpty() || telephone.length() != 11) {
            return false;
        }

        for (String str : phonePrefix) {
            if (telephone.startsWith("7" + str)) {
                return true;
            }
        }
        return false;
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
        }
        if (user.getBirthDate().matches(BIRTH_DATE_PATTERN)) {
            errorMap.put(DtoUser.class.getSimpleName() + ".birthDate", "wrong pattern for birthDate");
        }
        return errorMap;
    }
}
