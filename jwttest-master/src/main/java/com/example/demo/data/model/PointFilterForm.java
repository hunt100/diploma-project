package com.example.demo.data.model;

import lombok.Data;

@Data
public class PointFilterForm extends BaseForm {
    private String pointStatus;
    private String dangerLevel;
}
