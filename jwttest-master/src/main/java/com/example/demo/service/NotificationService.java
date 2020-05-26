package com.example.demo.service;

import com.example.demo.data.entity.Notification;
import com.example.demo.data.entity.Point;
import com.example.demo.data.entity.UserProfile;
import com.example.demo.data.enums.NotificationStatus;
import com.example.demo.data.enums.PointStatus;
import com.example.demo.data.enums.ValueKey;
import com.example.demo.data.model.NotificationForm;
import com.example.demo.repository.NotificationRepository;
import com.example.demo.service.emailgateway.EmailGateway;
import com.example.demo.service.mapper.NotificationMapper;
import com.example.demo.service.pebble.PebbleConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final NotificationEventService eventService;
    private final PebbleConstructor pebbleConstructor;
    private final NotificationMapper notificationMapper;
    private final EmailGateway emailGateway;

    @Value("${email.message.url}")
    private String url;

    @Autowired
    public NotificationService(NotificationRepository notificationRepository, NotificationEventService eventService, PebbleConstructor pebbleConstructor, NotificationMapper notificationMapper, EmailGateway emailGateway) {
        this.notificationRepository = notificationRepository;
        this.eventService = eventService;
        this.pebbleConstructor = pebbleConstructor;
        this.notificationMapper = notificationMapper;
        this.emailGateway = emailGateway;
    }

    @Transactional
    public List<Notification> findAll() {
        return notificationRepository.findAll();
    }

    @Transactional
    public Notification createNotification(Notification notification) {
        return notificationRepository.save(notification);
    }

    @Transactional
    public Notification createNotification(Point point, PointStatus status) {
        int stat = status.getStatusCode();
        if (stat == 100) {
           return null;
        }
        Notification notification = createNotification(new Notification());
        Map<String, Object> params = new HashMap<>();
        String unformattedText = "";
        switch (stat) {
            case 200:
                unformattedText = eventService.getNotificationEventByKey(ValueKey.VALIDATE_EVENT_NOTIFICATION).getText();
                url += "/?point=" + point.getId();
                params.put("url", url);
                break;
            case 300:
                unformattedText = eventService.getNotificationEventByKey(ValueKey.FINISH_EVENT_NOTIFICATION).getText();
                break;
            case 301:
                unformattedText = eventService.getNotificationEventByKey(ValueKey.CANCEL_EVENT_NOTIFICATION).getText();
                break;
            default: log.error("Unexpected notification case"); return null;
        }
        params.put("id", point.getId());
        params.put("pointName", point.getName());


        notification.setMessage(pebbleConstructor.createCompleteMessage(params, unformattedText));
        notification.setNotificationStatus(NotificationStatus.CREATED);
        notification.setPoint(point);
        log.info("User point with id: {} by user profile id: {}", point.getId(), point.getUserProfile().getId());
        notification.setUserProfile(point.getUserProfile());

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();
        log.info("Point with id {} validated by {}", point.getId(), currentPrincipalName);

        emailGateway.sendMessage(point.getUserProfile().getEmail(), "Статус точки", notification.getMessage());
        updateNotification(notification.getId(), notification);
        return notification;
    }

    @Transactional
    public Notification findNotificationById(Long id) {
        Optional<Notification> notification = notificationRepository.findById(id);
        if (!notification.isPresent()) {
            log.warn("Not founded user with Id: {}", id);
            throw new IllegalArgumentException("id - " + id);
        }
        return notification.get();
    }

    @Transactional
    public void deleteNotification(Long id) {
        notificationRepository.deleteById(id);
    }

    @Transactional
    public void updateNotification(Long id, Notification notification) {
        Optional<Notification> foundedNotification = notificationRepository.findById(id);
        if (foundedNotification.isPresent()) {
            notification.setId(id);
            notificationRepository.save(notification);
        } else {
            log.warn("Not founded user with Id: {}", id);
            throw new IllegalArgumentException("Illegal UserProfile id -" + notification.getId());
        }
    }

    @Transactional
    public List<Notification> findAllByNotificationStatus (NotificationStatus notificationStatus) {
        return notificationRepository.findAllByNotificationStatus(notificationStatus);
    }

    @Transactional
    public List<Notification> findAllByUserProfileAndNotificationStatus(UserProfile userProfile, NotificationStatus notificationStatus) {
        return notificationRepository.findAllByUserProfileAndNotificationStatus(userProfile, notificationStatus);
    }

    @Transactional
    public List<Notification> findAllByUserProfile(UserProfile userProfile) {
        return notificationRepository.findAllByUserProfile(userProfile);
    }

    @Transactional
    public Long updateNotificationStatus(Long id, NotificationStatus notificationStatus) {
        Notification notification = findNotificationById(id);
        notification.setNotificationStatus(notificationStatus);
        notification.setPerformedAt(LocalDateTime.now());
        updateNotification(id, notification);
        return notification.getId();
    }

    public NotificationForm entityToModel(Notification notification) {
        return notificationMapper.entityToModel(notification);
    }

    public List<NotificationForm> listEntitiesToModels(List<Notification> notifications) {
        return notifications.stream().map(notificationMapper::entityToModel).collect(Collectors.toList());
    }
}
