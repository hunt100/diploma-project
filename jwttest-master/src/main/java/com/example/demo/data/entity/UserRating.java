package com.example.demo.data.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class UserRating extends BaseEntity{

    @Column(name = "is_good_rate")
    private boolean rate;

    @Column(name = "is_active")
    private boolean isActive;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "user_profile_id", updatable = false)
    private UserProfile userProfile; //id

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "point_id", updatable = false)
    private Point point; //id

    @Override
    public String toString() {
        return "UserRating{" +
                "rate=" + rate +
                ", isActive=" + isActive +
                '}';
    }
}
