package com.example.demo.repository;

import com.example.demo.data.entity.DaoUser;
import com.example.demo.data.entity.ValidateToken;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ValidateTokenRepository extends BaseRepository<ValidateToken> {
    ValidateToken findByToken(String token);

    List<ValidateToken> findAllByExpireDateIsBeforeAndActiveIsFalseAndUserAndExpiredIsFalse(LocalDateTime expireDate, DaoUser user);
}
