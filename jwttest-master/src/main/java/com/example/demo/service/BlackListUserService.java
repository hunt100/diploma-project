package com.example.demo.service;

import com.example.demo.data.entity.BlackListUser;
import com.example.demo.data.entity.UserProfile;
import com.example.demo.data.model.BlackListModelForm;
import com.example.demo.repository.BlackListUserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class BlackListUserService {
    private final BlackListUserRepository blackListUserRepository;

    @Autowired
    public BlackListUserService(BlackListUserRepository blackListUserRepository) {
        this.blackListUserRepository = blackListUserRepository;
    }

    @Transactional
    public Long addToBlackList(BlackListModelForm form, UserProfile userProfile) {
        if (checkAlreadyInBlackList(userProfile)) {
            log.info("UserProfile: {} was already have active ban. The system doesn't do anything!", form.getId());
            return blackListUserRepository.findAllByUserProfileIdAndActive(userProfile.getId(), true).get(0).getId();
        }
        BlackListUser blackListUser = new BlackListUser(
                userProfile.getUser().getId(),
                userProfile.getId(),
                userProfile.getUser().getUsername(),
                userProfile.getFirstName(),
                userProfile.getLastName(),
                userProfile.getPatronymic(),
                userProfile.getIin(),
                userProfile.getTelephone(),
                form.getPurpose(),
                true,
                null);
        log.info("Adding UserProfile: {} to blackList with following reason: {}", form.getId(), form.getPurpose());
        return blackListUserRepository.save(blackListUser).getId();
    }

    @Transactional
    public boolean checkAlreadyInBlackList(UserProfile userProfile) {
        return !blackListUserRepository.findAllByUserProfileIdAndActive(userProfile.getId(), true).isEmpty();
    }

    @Transactional
    public Long removeFromBlackList(UserProfile userProfile) {
        if (!checkAlreadyInBlackList(userProfile)) {
            log.info("UserProfile: {} didn't have any active bans!", userProfile.getId());
            return 0L;
        }
        BlackListUser currentBlackListUser = blackListUserRepository.findAllByUserProfileIdAndActive(userProfile.getId(), true).get(0);
        currentBlackListUser.setActive(false);
        blackListUserRepository.save(currentBlackListUser);
        log.info("Successfully unBan UserProfile: {}", userProfile.getId());
        return currentBlackListUser.getId();
    }

    @Transactional
    public BlackListUser findBlackListUserByUserProfileId(Long userId) {
        List<BlackListUser> blackList = blackListUserRepository.findAllByUserProfileIdAndActive(userId, true);
        if (blackList.size() == 1) {
            return blackList.get(0);

        }
        log.warn("Unexpected situation! There're more than one active ban for current user: ");
        BlackListUser mostYoung = blackList.get(0);
        for (BlackListUser u : blackList) {
            log.warn("Banned User replay! Id in table: {}. Is active: {}", u.getId(), u.getActive());
            if (mostYoung.getCreatedAt().isBefore(u.getCreatedAt())) {
                mostYoung = u;
            }
        }
        return mostYoung;
    }
}
