package com.example.demo.controller;

import com.example.demo.data.entity.BlackListUser;
import com.example.demo.data.entity.DaoUser;
import com.example.demo.data.entity.UserProfile;
import com.example.demo.data.entity.ValidateToken;
import com.example.demo.data.model.DtoUser;
import com.example.demo.data.model.JwtRequest;
import com.example.demo.data.model.PasswordResetForm;
import com.example.demo.service.BlackListUserService;
import com.example.demo.service.JwtUserDetailsService;
import com.example.demo.service.UserProfileService;
import com.example.demo.service.ValidTokenService;
import com.example.demo.service.validator.UserValidator;
import com.example.demo.util.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/v1/api")
public class JwtAuthenticationController {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;
    private final JwtUserDetailsService userDetailsService;
    private final UserProfileService userProfileService;
    private final UserValidator userValidator;
    private final ValidTokenService validTokenService;
    private final BlackListUserService blackListUserService;

    @Autowired
    public JwtAuthenticationController(AuthenticationManager authenticationManager,
                                       JwtTokenUtil jwtTokenUtil,
                                       JwtUserDetailsService userDetailsService,
                                       UserProfileService userProfileService,
                                       UserValidator userValidator,
                                       ValidTokenService validTokenService,
                                       BlackListUserService blackListUserService) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenUtil = jwtTokenUtil;
        this.userDetailsService = userDetailsService;
        this.userProfileService = userProfileService;
        this.userValidator = userValidator;
        this.validTokenService = validTokenService;
        this.blackListUserService = blackListUserService;
    }

    @PostMapping(value = "/authenticate")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody JwtRequest authenticationRequest) throws Exception {
            authenticate(authenticationRequest.getUsername(), authenticationRequest.getPassword());
            final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getUsername());
            final String token = jwtTokenUtil.generateToken(userDetails);

            Map<String, Object> customResponse = new HashMap<>();
            UserProfile currentUser = userProfileService.findByUser(userDetailsService.findUserByUsername(authenticationRequest.getUsername()));
            if (blackListUserService.checkAlreadyInBlackList(currentUser)) {
                BlackListUser blackListUser = blackListUserService.findBlackListUserByUserProfileId(currentUser.getId());
                customResponse.put("username", blackListUser.getUsername());
                customResponse.put("purpose", blackListUser.getPurpose());
                return ResponseEntity.ok(customResponse);
            }

            customResponse.put("jwttoken", token);
            customResponse.put("userData", userProfileService.entityToModel(currentUser));
            return ResponseEntity.ok(customResponse);
    }

    @PostMapping(value = "/register")
    public ResponseEntity<?> saveUser (@RequestBody DtoUser user) {
        Map<String, String> errorMap = userValidator.checkValidation(user);
            if (errorMap.size() == 0) {
                DaoUser created = userDetailsService.save(user);
                UserProfile userProfile = userProfileService.createUserProfile(user, created);
                validTokenService.confirmRegistration(userProfile);
                return ResponseEntity.ok(created);
            } else {
                return  ResponseEntity.badRequest().body(errorMap);
            }
    }

    @PostMapping(value = "/forgot")
    public ResponseEntity<?> forgotPassword(@RequestBody PasswordResetForm form) {
        DaoUser user = userDetailsService.findUserByUsername(form.getLogin());
        if (user == null) {
            return ResponseEntity.ok().build();
        }
        ValidateToken token = validTokenService.getToken(user);
        UserProfile userProfile = userProfileService.findByUser(user);
        validTokenService.sendPasswordResetToken(token, userProfile);
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/forgot/{token}")
    public ResponseEntity<?> updatePassword(@PathVariable String token, @RequestBody PasswordResetForm form) {
        boolean isValid = validTokenService.validateToken(token);
        if (!isValid) {
            return ResponseEntity.badRequest().build();
        }
        ValidateToken tokenEntity = validTokenService.findEntityByToken(token);
        //Здесь бы тоже неплохо заиметь валидацию пароля
        validTokenService.changePassword(tokenEntity, form.getPassword());
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/confirmation/{token}")
    public ResponseEntity<?> updateUserStatus(@PathVariable String token) {
        boolean isValid = validTokenService.validateToken(token);
        if (!isValid) {
            return ResponseEntity.badRequest().build();
        }
        ValidateToken tokenEntity = validTokenService.findEntityByToken(token);
        validTokenService.updateUserStatus(tokenEntity, tokenEntity.getUser());
        return ResponseEntity.ok().build();
    }

    private void authenticate(String username, String password) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException e) {
            throw new Exception("USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            throw new Exception("INVALID_CREDENTIALS", e);
        }
    }
}
