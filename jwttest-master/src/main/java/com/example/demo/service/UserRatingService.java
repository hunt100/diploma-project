package com.example.demo.service;

import com.example.demo.data.entity.UserProfile;
import com.example.demo.data.entity.UserRating;
import com.example.demo.repository.UserRatingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserRatingService {
    private final UserRatingRepository userRatingRepository;
    private final PointService pointService;

    @Autowired
    public UserRatingService(UserRatingRepository userRatingRepository, PointService pointService) {
        this.userRatingRepository = userRatingRepository;
        this.pointService = pointService;
    }

    @Transactional
    public boolean leaveALike(Long pointId, UserProfile userProfile) {
        List<UserRating> userRatings = userRatingRepository.findAllByPoint_IdAndUserProfile(pointId, userProfile);
        if (userRatings.isEmpty()) {
            UserRating ur = new UserRating();
            ur.setActive(true);
            ur.setRate(true);
            ur.setUserProfile(userProfile);
            ur.setPoint(pointService.findPointById(pointId));
            userRatingRepository.save(ur);
            return true;
        } else {
            userRatings.get(0).setRate(!userRatings.get(0).isRate());
        }
        userRatingRepository.save(userRatings.get(0));
        return userRatings.get(0).isRate();
    }

    @Transactional
    public boolean isLikeExist(Long pointId, UserProfile userProfile) {
        return !userRatingRepository.findAllByPoint_IdAndUserProfile(pointId, userProfile).isEmpty() && userRatingRepository.findAllByPoint_IdAndUserProfile(pointId, userProfile).get(0).isRate();
    }
}
