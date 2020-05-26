package com.example.demo.repository;

import com.example.demo.data.entity.DaoUser;
import com.example.demo.data.entity.UserProfile;
import org.springframework.stereotype.Repository;

@Repository
public interface UserProfileRepository extends BaseRepository<UserProfile> {
    UserProfile findByUser(DaoUser user);

    UserProfile findByEmail(String email);
}
