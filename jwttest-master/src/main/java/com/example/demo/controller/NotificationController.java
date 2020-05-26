package com.example.demo.controller;

import com.example.demo.data.entity.DaoUser;
import com.example.demo.data.entity.Notification;
import com.example.demo.data.entity.UserProfile;
import com.example.demo.data.enums.NotificationStatus;
import com.example.demo.data.model.NotificationForm;
import com.example.demo.service.JwtUserDetailsService;
import com.example.demo.service.NotificationService;
import com.example.demo.service.UserProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private JwtUserDetailsService userDetailsService;

    @Autowired
    private UserProfileService userProfileService;

    @GetMapping
    public ResponseEntity<?> findAllNotifications() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();
        DaoUser daoUser = userDetailsService.findUserByUsername(currentPrincipalName);
        UserProfile userProfile = userProfileService.findByUser(daoUser);
        List<NotificationForm> unreadNotifications = new ArrayList<>();
        for (Notification n : notificationService.findAllByUserProfile(userProfile)) {
            unreadNotifications.add(notificationService.entityToModel(n));
        }
        Map<String, Object> response = new HashMap<>();
        response.put("unread_count", unreadNotifications.stream().filter(n -> n.getNotificationStatus().equals(NotificationStatus.CREATED)).count());
        response.put("notifications", unreadNotifications);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/update")
    public ResponseEntity<?> updateDeliveredNotification() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();
        DaoUser daoUser = userDetailsService.findUserByUsername(currentPrincipalName);
        UserProfile userProfile = userProfileService.findByUser(daoUser);
        List<Notification> unreadNotifications = notificationService.findAllByUserProfileAndNotificationStatus(userProfile, NotificationStatus.CREATED);
        for(Notification notification : unreadNotifications) {
            notificationService.updateNotificationStatus(notification.getId(), NotificationStatus.PERFORMED);
        }
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MODERATOR')")
    @GetMapping("/status")
    public ResponseEntity<?> filterByStatus(@RequestParam("status") List<String> status) {
        List<Notification> result = new ArrayList<>();
        for (String st : status) {
            result.addAll(notificationService.findAllByNotificationStatus(NotificationStatus.valueOf(st)));
        }
        return ResponseEntity.ok(notificationService.listEntitiesToModels(result));
    }
}
