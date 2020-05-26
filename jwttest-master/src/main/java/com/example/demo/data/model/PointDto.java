package com.example.demo.data.model;

import lombok.Data;

import java.util.List;

@Data
public class PointDto extends BaseForm {
    private Double lat;
    private Double lng;
    private String name;
    private String description;
    private String pointStatus;
    private String iconName;
    private String iconUrl;
    private String dangerLevel;
    private String images;
    private PointUserProfileDto userProfileDto;
    private List<UserRatingForm> userRating;
    private long rating;

    @Data
    public static class PointUserProfileDto {
        private Long id;
        private String firstName;
        private String lastName;
        private String patronymic;
        private String initialsAvatar;
    }
}
