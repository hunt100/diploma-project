package com.example.demo.repository;

import com.example.demo.data.entity.DaoUser;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DaoUserRepository extends BaseRepository<DaoUser> {
    DaoUser findByUsername(String username);

    List<DaoUser> findAllByUsernameContaining(String username);
}
