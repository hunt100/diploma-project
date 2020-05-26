package com.example.demo.service;

import com.example.demo.data.entity.Authority;
import com.example.demo.repository.AuthorityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;

@Service
public class AuthorityService {
    private final AuthorityRepository authorityRepository;

    @Autowired
    public AuthorityService(AuthorityRepository authorityRepository) {
        this.authorityRepository = authorityRepository;
    }

    @PostConstruct
    public void init() {
        List<Authority> checkIsExist = findAll();
        if (checkIsExist == null || checkIsExist.size() == 0) {
            String[] names = new String[]{"ROLE_ADMIN", "ROLE_MODERATOR", "ROLE_USER"};
            for (String roleName: names) {
                Authority a = new Authority();
                a.setName(roleName);
                save(a);
            }

        }
    }

    public List<Authority> findAll() {
        return authorityRepository.findAll();
    }

    public Authority save(Authority authority) {
        return authorityRepository.save(authority);
    }

    public Authority findByName(String name) {
        return authorityRepository.findByName(name);
    }


}
