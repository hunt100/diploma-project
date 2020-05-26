package com.example.demo.repository;

import com.example.demo.data.entity.UserProfile;
import com.example.demo.data.entity.UserRating;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRatingRepository extends BaseRepository<UserRating> {
    List<UserRating> findAllByPoint_IdAndUserProfile(Long pointId, UserProfile userProfile);
}
