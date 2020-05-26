package com.example.demo.repository;

import com.example.demo.data.entity.BlackListUser;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BlackListUserRepository extends BaseRepository<BlackListUser> {
    List<BlackListUser> findAllByUserProfileIdAndActive(Long userId, boolean active);
}
