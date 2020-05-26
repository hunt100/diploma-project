package com.example.demo.data.model;

import lombok.Data;

import java.io.Serializable;

@Data
public abstract class BaseForm implements Serializable {
    private Long id;
}
