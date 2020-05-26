package com.example.demo.data.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Data
@Table(name = "authorities")
public class Authority extends BaseEntity{

    @Column(name = "name", length = 50)
    private String name;
}