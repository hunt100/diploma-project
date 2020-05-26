package com.example.demo.data.model;

import com.example.demo.data.enums.NotificationStatus;
import lombok.Data;

@Data
public class NotificationForm extends BaseForm {
    private NotificationStatus notificationStatus;
    private String message;
    private String pointName;
    private String pointStatus;
    private Long pointId;
}
