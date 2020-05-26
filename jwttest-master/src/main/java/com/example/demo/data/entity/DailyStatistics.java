package com.example.demo.data.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "daily_statistics")
public class DailyStatistics extends BaseEntity {
    @Column(name = "created")
    private long created;

    @Column(name = "active")
    private long active;

    @Column(name = "cancel")
    private long cancel;

    @Column(name = "resolved")
    private long resolved;

    @Column(name = "date")
    private LocalDate date;
}
