package com.example.demo.service;

import com.example.demo.data.entity.DaoUser;
import com.example.demo.data.entity.UserProfile;
import com.example.demo.data.entity.ValidateToken;
import com.example.demo.data.enums.TokenType;
import com.example.demo.data.enums.ValueKey;
import com.example.demo.repository.ValidateTokenRepository;
import com.example.demo.service.emailgateway.EmailGateway;
import com.example.demo.service.pebble.PebbleConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
public class ValidTokenService {
    private final JwtUserDetailsService userService;
    private final ValidateTokenRepository resetTokenRepository;
    private final PebbleConstructor pebbleConstructor;
    private EmailGateway emailGateway;
    private NotificationEventService notificationEventService;

    @Value("${email.message.url}")
    private String url;

    @Autowired
    public ValidTokenService(JwtUserDetailsService userService, ValidateTokenRepository resetTokenRepository, PebbleConstructor pebbleConstructor, EmailGateway emailGateway, NotificationEventService notificationEventService) {
        this.userService = userService;
        this.resetTokenRepository = resetTokenRepository;
        this.pebbleConstructor = pebbleConstructor;
        this.emailGateway = emailGateway;
        this.notificationEventService = notificationEventService;
    }

    @Transactional
    public boolean validateToken(String token) {
        ValidateToken foundedToken = resetTokenRepository.findByToken(token);
        boolean isValid = true;
        if (foundedToken == null) {
            log.error("No such token in db: {}", token);
            return false;
        }
        if (LocalDateTime.now().isAfter(foundedToken.getExpireDate())) {
            log.error("Date expired, Expiration time: {}. Token: {}", foundedToken.getExpireDate().toString(), token);
            foundedToken.setExpired(true);
            resetTokenRepository.save(foundedToken);
            isValid = false;
        }
        if (foundedToken.isActive()) {
            log.error("Token already active. Token: {}", token);
            isValid = false;
        }
        return isValid;
    }

    @Transactional
    public ValidateToken findEntityByToken(String token) {
        return resetTokenRepository.findByToken(token);
    }

    @Transactional
    public Long changePassword(ValidateToken token, String plainPassword) {
        token.setActive(true);
        return userService.updatePassword(token.getUser(), plainPassword).getId();
    }

    @Transactional
    public void updateUserStatus(ValidateToken token, DaoUser user) {
        token.setActive(true);
        userService.updateUserStatus(user);
    }

    @Async
    public void sendPasswordResetToken(ValidateToken token, UserProfile userProfile) {
        String message = notificationEventService.getNotificationEventByKey(ValueKey.EMAIL_RESET_PASSWORD_NOTIFICATION).getText();
        String uurl = url + "/recover-password/";
        Map<String, Object> params = new HashMap<>();
        params.put("fio", userProfile.getFullNameInitials());
        params.put("login", userProfile.getUser().getUsername());
        params.put("link", uurl + token.getToken());
        String text = pebbleConstructor.createCompleteMessage(params, message);
        emailGateway.sendMessage(userProfile.getEmail(), "Восстановление пароля", text);
    }

    @Transactional
    public ValidateToken getToken(DaoUser user) {
        if (isExist(user)) {
            return resetTokenRepository.findAllByExpireDateIsBeforeAndActiveIsFalseAndUserAndExpiredIsFalse(LocalDateTime.now(), user).get(0);
        } else {
            return createToken(user, TokenType.PASSWORD_RESET);
        }
    }

    public void confirmRegistration(UserProfile userProfile) {
        ValidateToken vToken = createToken(userProfile.getUser(), TokenType.REGISTRATION_CONFIRM);
        String uurl = url + "/confirmation/";
        Map<String, Object> params = new HashMap<>();
        params.put("link", uurl + vToken.getToken());
        String text = pebbleConstructor.createCompleteMessage(params, notificationEventService.getNotificationEventByKey(ValueKey.REGISTRATION_CONFIRM).getText());
        emailGateway.sendMessage(userProfile.getEmail(), "Подтверждение регистрации", text);
    }

    private boolean isExist(DaoUser user) {
        return !resetTokenRepository.findAllByExpireDateIsBeforeAndActiveIsFalseAndUserAndExpiredIsFalse(LocalDateTime.now(), user).isEmpty();
    }

    private ValidateToken createToken(DaoUser user, TokenType type) {
        String uuid = UUID.randomUUID().toString();
        LocalDateTime expireDate = LocalDateTime.now().plusHours(1);
        ValidateToken token = new ValidateToken(uuid, expireDate, type ,false, false, user);
        return resetTokenRepository.save(token);
    }


}
