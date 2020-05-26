package com.example.demo.data.entity;

import com.example.demo.data.enums.NotificationStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "notification_logs")
public class Notification extends BaseEntity{

    @Column
    private LocalDateTime performedAt;

    @Column
    @Enumerated(value = EnumType.STRING)
    private NotificationStatus notificationStatus;

    @Column
    private String message;

    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY)
    private Point point;

    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY)
    private UserProfile userProfile;
}
