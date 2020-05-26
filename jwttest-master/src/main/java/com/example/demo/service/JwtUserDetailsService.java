package com.example.demo.service;

import com.example.demo.data.entity.Authority;
import com.example.demo.data.entity.DaoUser;
import com.example.demo.data.model.DtoUser;
import com.example.demo.repository.DaoUserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class JwtUserDetailsService implements UserDetailsService {
    @Autowired
    private DaoUserRepository userRepository;

    @Autowired
    private PasswordEncoder bcryptEncoder;

    @Autowired
    private AuthorityService authorityService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        DaoUser user = userRepository.findByUsername(username);
        if (user == null) {
            log.error("User with username: {} not found!", username);
            throw new UsernameNotFoundException("User with username: " + username + " not found!");
        }

        return new User(user.getUsername(), user.getPassword(),user.isEnabled(), true, true, true, getAuthorities(user.getRoles()));
    }

    private Collection<? extends GrantedAuthority> getAuthorities(Collection<Authority> myAuthority) {
        List<GrantedAuthority> authorities
                = new ArrayList<>();
        for (Authority role: myAuthority) {
            authorities.add(new SimpleGrantedAuthority(role.getName()));
        }
        return authorities;
    }

    public DaoUser save(DtoUser user) {
        DaoUser newUser = new DaoUser();
        newUser.setUsername(user.getUsername());
        newUser.setPassword(bcryptEncoder.encode(user.getPassword()));
        newUser.setEnabled(false);
        newUser.setRoles(Collections.singleton(authorityService.findByName("ROLE_USER")));
        return userRepository.save(newUser);
    }

    public DaoUser findUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public DaoUser updatePassword(DaoUser user, String plainPassword) {
        user.setPassword(bcryptEncoder.encode(plainPassword));
        return userRepository.save(user);
    }

    public List<DaoUser> findAllLikeUsername(String username) {
        return userRepository.findAllByUsernameContaining(username);
    }

    public void updateUserStatus(DaoUser user) {
        user.setEnabled(true);
        userRepository.save(user);
    }
}
