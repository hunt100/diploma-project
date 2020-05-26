package com.example.demo.data.entity;

import com.example.demo.data.enums.DangerLevel;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Entity
@Data
public class PointCategory extends BaseEntity {
    @Column(name = "icon_name")
    private String iconName;

    @Column(name = "icon_url")
    private String iconUrl;

    @Column(name = "danger_level")
    @Enumerated(value = EnumType.STRING)
    DangerLevel dangerLevel;
}
