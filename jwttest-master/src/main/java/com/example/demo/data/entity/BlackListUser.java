package com.example.demo.data.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "blacklist_users")
public class BlackListUser extends BaseEntity{

    @Column
    private Long userId;

    @Column
    private Long userProfileId;

    @Column
    private String username;

    @Column
    private String firstName;

    @Column
    private String lastName;

    @Column
    private String patronymic;

    @Column
    private String iin;

    @Column
    private String phone;

    @Column
    private String purpose;

    @Column
    private Boolean active;

    @Column
    private LocalDateTime lockedUntil;
}
