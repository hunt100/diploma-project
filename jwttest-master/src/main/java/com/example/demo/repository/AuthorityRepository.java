package com.example.demo.repository;

import com.example.demo.data.entity.Authority;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorityRepository extends BaseRepository<Authority> {
    Authority findByName(String name);
}
