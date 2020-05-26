package com.example.demo.data.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@Table(name = "user_profiles")
public class UserProfile extends BaseEntity {
    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "patronymic")
    private String patronymic;

    @Column(name = "birth_date", nullable = false)
    private String birthDate;

    @Column(name = "telephone", nullable = false)
    private String telephone;

    @Column(name = "iin", nullable = false)
    private String iin;

    @Column(name = "email", nullable = false)
    private String email;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private DaoUser user;

    @OneToMany(mappedBy = "userProfile")
    private List<UserRating> userRate;

    @OneToMany(mappedBy = "userProfile", fetch = FetchType.LAZY)
    private List<Point> points;

    @Column(name = "avatar")
    private String initialsAvatar;

    public String getFullName () {
        return this.patronymic == null ? this.lastName + " " + this.firstName : this.lastName + " " + this.firstName + " " + this.patronymic;
    }

    public String getFullNameInitials () {
        return this.patronymic == null ? this.lastName + " " + this.firstName.charAt(0) + "." : this.lastName + " " + this.firstName.charAt(0) + ". " +this.patronymic + ".";
    }

    @Override
    public String toString() {
        return "UserProfile{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", patronymic='" + patronymic + '\'' +
                ", birthDate='" + birthDate + '\'' +
                ", telephone='" + telephone + '\'' +
                ", iin='" + iin + '\'' +
                ", email='" + email + '\'' +
                ", user=" + user +
                ", userRate=" + userRate +
                ", points=" + points +
                '}';
    }
}
