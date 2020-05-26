package com.example.demo.repository;

import com.example.demo.data.entity.Notification;
import com.example.demo.data.entity.UserProfile;
import com.example.demo.data.enums.NotificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findAllByNotificationStatus (NotificationStatus notificationStatus);

    List<Notification> findAllByUserProfileAndNotificationStatus (UserProfile userProfile, NotificationStatus notificationStatus);

    List<Notification> findAllByUserProfile (UserProfile userProfile);
}
