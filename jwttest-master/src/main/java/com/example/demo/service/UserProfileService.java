package com.example.demo.service;

import com.example.demo.data.entity.Authority;
import com.example.demo.data.entity.DaoUser;
import com.example.demo.data.entity.UserProfile;
import com.example.demo.data.model.DtoUser;
import com.example.demo.data.model.PageModelForm;
import com.example.demo.repository.UserProfileRepository;
import com.example.demo.service.mapper.UserProfileMapper;
import com.example.demo.util.TranscriptorUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class UserProfileService {
    private final UserProfileRepository userProfileRepository;
    private final UserProfileMapper userProfileMapper;
    private final JwtUserDetailsService userService;
    private final BlackListUserService blackListUserService;

    @Autowired
    public UserProfileService(UserProfileRepository userProfileRepository, UserProfileMapper userProfileMapper, JwtUserDetailsService userService, BlackListUserService blackListUserService) {
        this.userProfileRepository = userProfileRepository;
        this.userProfileMapper = userProfileMapper;
        this.userService = userService;
        this.blackListUserService = blackListUserService;
    }

    public List<DtoUser> findAll() {
        List<UserProfile> userProfiles = userProfileRepository.findAll();
        return convertListOfEntityToModels(userProfiles);
    }

    public PageModelForm<DtoUser> findAllByPages(int currentPage, int currentSize) {
        Pageable page = PageRequest.of(currentPage, currentSize, Sort.by(Sort.Direction.DESC, "id"));
        Page<UserProfile> userProfilesPage = userProfileRepository.findAll(page);
        List<DtoUser> forms = convertListOfEntityToModels(userProfilesPage.getContent());
        return new PageModelForm<>(
                userProfilesPage.getNumber(),
                userProfilesPage.getNumberOfElements(),
                userProfilesPage.getTotalPages(),
                userProfilesPage.getTotalElements(),
                forms);
    }

    public List<DtoUser> findAllLikeUsername(String username) {
        List<UserProfile> userProfiles = new ArrayList<>();
        for (DaoUser d : userService.findAllLikeUsername(username)) {
            userProfiles.add(userProfileRepository.findByUser(d));
        }
        return convertListOfEntityToModels(userProfiles);
    }

    private List<DtoUser> convertListOfEntityToModels(List<UserProfile> userProfiles) {
        List<DtoUser> forms = new ArrayList<>();
        for (UserProfile u : userProfiles) {
            DtoUser dtoUser = entityToModel(u);
            dtoUser.setBanned(blackListUserService.checkAlreadyInBlackList(u));
            forms.add(dtoUser);
        }
        return forms;
    }

    public UserProfile createUserProfile(UserProfile userProfile) {
        return userProfileRepository.save(userProfile);
    }

    public UserProfile createUserProfile(DtoUser dtoUser, DaoUser daoUser) {
        UserProfile userProfile = userProfileMapper.modelToEntity(dtoUser);
        userProfile.setUser(daoUser);
        String avatar = "https://avatars.dicebear.com/v2/initials/%s.svg";
        String initials = "" + TranscriptorUtil.translateToLatin(dtoUser.getFirstName()).toUpperCase().charAt(0) +
                TranscriptorUtil.translateToLatin(dtoUser.getLastName()).toUpperCase().charAt(0);
        avatar = String.format(avatar, initials);
        userProfile.setInitialsAvatar(avatar);
        return userProfileRepository.save(userProfile);
    }

    public UserProfile findUserProfileById(Long id) {
        Optional<UserProfile> userProfile = userProfileRepository.findById(id);
        if (!userProfile.isPresent()) {
            log.warn("Not founded user with Id: {}", id);
            throw new IllegalArgumentException("id - " + id);
        }
        return userProfile.get();
    }

    public void deleteUserProfile(Long id) {
        userProfileRepository.deleteById(id);
    }

    public void updateUserProfile(Long id, UserProfile userProfile) {
        Optional<UserProfile> foundedUserProfile = userProfileRepository.findById(id);
        if (foundedUserProfile.isPresent()) {
            userProfile.setId(id);
            userProfile.setUser(foundedUserProfile.get().getUser());
            userProfile.setPoints(foundedUserProfile.get().getPoints());
            userProfile.setUserRate(foundedUserProfile.get().getUserRate());

            //Добавлено на тот случай, если юзер смог как-то изменить свои первоначальные данные: имя, фамилия, отчество, иин
            //По хорошему, здесь бы валидатор тоже чтобы отрабатывал
            userProfile.setFirstName(foundedUserProfile.get().getFirstName());
            userProfile.setLastName(foundedUserProfile.get().getLastName());
            userProfile.setPatronymic(foundedUserProfile.get().getPatronymic());
            userProfile.setIin(foundedUserProfile.get().getIin());
            userProfileRepository.save(userProfile);
        } else {
            log.warn("Not founded user with Id: {}", id);
            throw new IllegalArgumentException("Illegal UserProfile id -" + userProfile.getId());
        }
    }

    public UserProfile findByUser(DaoUser user) {
        return userProfileRepository.findByUser(user);
    }

    public UserProfile findByEmail(String email) { return userProfileRepository.findByEmail(email); }

    public DtoUser entityToModelPartially (UserProfile userProfile) {
        return userProfileMapper.entityToModelPartially(userProfile);
    }

    public DtoUser entityToModel(UserProfile userProfile) {
        return userProfileMapper.entityToModel(userProfile);
    }

    public UserProfile modelToEntity(DtoUser dtoUser) {
        return userProfileMapper.modelToEntity(dtoUser);
    }

    public List<DtoUser> getAllBannedUsers() {
        List<UserProfile> users = new ArrayList<>();
        for (UserProfile u : userProfileRepository.findAll()) {
            for (Authority a : u.getUser().getRoles()) {
                if (!a.getName().equals("ROLE_ADMIN")) {
                    users.add(u);
                }
            }
        }
        List<DtoUser> dtoUsers = new ArrayList<>();
        users.forEach(u -> dtoUsers.add(entityToModel(u)));
        return dtoUsers;
    }
}
