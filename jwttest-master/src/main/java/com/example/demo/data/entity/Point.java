package com.example.demo.data.entity;

import com.example.demo.data.enums.PointStatus;
import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "points")
@Data
public class Point extends BaseEntity {
    @Column(name = "latitude")
    private double lat;
    @Column(name = "longitude")
    private double lng;

    @Column(name = "name")
    private String name;

    @Column(name = "short_description")
    private String description;

    @Column(name = "status", nullable = false)
    @Enumerated(value = EnumType.STRING)
    private PointStatus pointStatus;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private PointCategory pointCategory;

    @OneToMany(mappedBy = "point")
    private List<UserRating> userRate;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "user_profile_id", updatable = false)
    private UserProfile userProfile; //id

    @Column(name = "images", columnDefinition="TEXT")
    private String images;

    @Override
    public String toString() {
        return "Point{" +
                "lat=" + lat +
                ", lng=" + lng +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", pointStatus=" + pointStatus +
                ", pointCategory=" + pointCategory +
                ", userRate=" + userRate +
                ", images='" + images + '\'' +
                '}';
    }
}
