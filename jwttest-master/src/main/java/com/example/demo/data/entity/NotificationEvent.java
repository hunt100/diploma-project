package com.example.demo.data.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table
@Data
public class NotificationEvent extends BaseEntity {

    @Column(unique = true)
    private String keyValue;

    @Column(name = "message_text", columnDefinition = "TEXT")
    private String text;

    public NotificationEvent() {
    }

    public NotificationEvent(String keyValue, String text) {
        super();
        this.keyValue = keyValue;
        this.text = text;
    }
}
