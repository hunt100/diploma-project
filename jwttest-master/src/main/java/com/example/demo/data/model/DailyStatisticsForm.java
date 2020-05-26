package com.example.demo.data.model;

import lombok.Data;

@Data
public class DailyStatisticsForm extends BaseForm {
    private long created;
    private long active;
    private long cancel;
    private long resolved;
    private String date;
}
