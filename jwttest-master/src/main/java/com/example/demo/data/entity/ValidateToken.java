package com.example.demo.data.entity;

import com.example.demo.data.enums.TokenType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "validate_tokens")
public class ValidateToken extends BaseEntity{

    @Column(name = "token")
    private String token;

    @Column(name = "expireDate")
    private LocalDateTime expireDate;

    @Column(name = "type", nullable = false)
    @Enumerated(value = EnumType.STRING)
    private TokenType type;

    @Column(name = "active")
    private boolean active;

    @Column(name = "expired")
    private boolean expired;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private DaoUser user;
}
