package com.example.demo.service;

import com.example.demo.data.entity.NotificationEvent;
import com.example.demo.data.enums.ValueKey;
import com.example.demo.repository.NotificationEventRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;

@Service
@Slf4j
public class NotificationEventService {
    private final NotificationEventRepository notificationEventRepository;

    @Autowired
    public NotificationEventService(NotificationEventRepository notificationEventRepository) {
        this.notificationEventRepository = notificationEventRepository;
    }

    @PostConstruct
    protected void init() {
        if (notificationEventRepository.findAll().isEmpty()) {
            notificationEventRepository.save(new NotificationEvent( "point.status.validated", "Ваша точка [{{id}}]-\"{{pointName}}\" прошла предварительный анализ и была подтверждена. Ссылка на точку: {{url}}"));
            notificationEventRepository.save(new NotificationEvent( "point.status.finished", "Ваша точка [{{id}}]-\"{{pointName}}\" была решена. Спасибо, что делаете наш город лучше!"));
            notificationEventRepository.save(new NotificationEvent( "point.status.cancelled", "Ваша точка [{{id}}]-\"{{pointName}}\" не прошла предварительного анализа и была отменена."));
            notificationEventRepository.save(new NotificationEvent( "email.reset.password", "Уважаемый {{fio}}. С Вашего аккаунта {{login}} поступил запрос на восстановление пароля. Чтобы получить новый пароль, пройдите по ссылке ниже: \n" +
                    "{{link}}\n" +
                    ". Если вы не делали запроса для получения пароля, то просто проигнорируйте данное письмо."));
            log.info("Notification events are added");
        }
        log.info("Notification event already added...continue");
    }

    @Transactional
    public NotificationEvent getNotificationEventByKey(ValueKey key) {
        return notificationEventRepository.findByKeyValue(key.getKeyBdValue());
    }
}
