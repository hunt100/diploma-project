package com.example.demo.controller;

import com.example.demo.data.entity.UserProfile;
import com.example.demo.data.model.BlackListModelForm;
import com.example.demo.data.model.DtoUser;
import com.example.demo.service.BlackListUserService;
import com.example.demo.service.JwtUserDetailsService;
import com.example.demo.service.UserProfileService;
import com.example.demo.util.JwtTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1/api/profile")
@Slf4j
public class UserProfileController {
    private final JwtTokenUtil jwtTokenUtil;
    private final UserProfileService userProfileService;
    private final JwtUserDetailsService userDetailsService;
    private final BlackListUserService blackListUserService;

    @Autowired
    public UserProfileController(JwtTokenUtil jwtTokenUtil, UserProfileService userProfileService, JwtUserDetailsService userDetailsService, BlackListUserService blackListUserService) {
        this.jwtTokenUtil = jwtTokenUtil;
        this.userProfileService = userProfileService;
        this.userDetailsService = userDetailsService;
        this.blackListUserService = blackListUserService;
    }

    @GetMapping
    public ResponseEntity<DtoUser> getCurrentUser(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        token = token.substring(7);
        String username = jwtTokenUtil.getUsernameFromToken(token);
        UserProfile currentUser = userProfileService.findByUser(userDetailsService.findUserByUsername(username));
        DtoUser response = userProfileService.entityToModel(currentUser);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DtoUser> getUserProfileById(@PathVariable Long id) {
        UserProfile userProfile = userProfileService.findUserProfileById(id);
        DtoUser response = userProfileService.entityToModel(userProfile);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/all")
    public ResponseEntity<List<DtoUser>> getAllUsers() {
        List<DtoUser> response = userProfileService.findAll();
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<?> updateUserData(@RequestBody DtoUser dtoUser) {
        UserProfile userProfile = userProfileService.modelToEntity(dtoUser);
        try {
            userProfileService.updateUserProfile(userProfile.getId(), userProfile);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Something wrong is happen! Error: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/list")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MODERATOR')")
    public ResponseEntity<?> getUsersByPages(
            @RequestParam(name = "currentPage") int currentPage,
            @RequestParam(name = "currentSize") int currentSize
    ) {
        return ResponseEntity.ok(userProfileService.findAllByPages(currentPage,currentSize));
    }

    @GetMapping("find/{username}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MODERATOR')")
    public ResponseEntity<List<DtoUser>> getUserByUsername(@PathVariable String username) {
        return ResponseEntity.ok(userProfileService.findAllLikeUsername(username));
    }

    @GetMapping("/ban")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> getAllBanUser() {
        return ResponseEntity.ok(userProfileService.getAllBannedUsers());
    }

    @PostMapping("/ban")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> banUser(@RequestBody BlackListModelForm form) {
        return makeBanSystemDecision(form, "ban");
    }

    @PostMapping("/unban")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> unBanUser(@RequestBody BlackListModelForm form) {
        return makeBanSystemDecision(form, "unban");
    }

    private ResponseEntity<Map<String, Object>> makeBanSystemDecision(BlackListModelForm form, String operation) {
        UserProfile userProfile = userProfileService.findUserProfileById(form.getId());
        if (userProfile == null) {
            ResponseEntity.badRequest().build();
        }
        Long banId = 0L;
        if (operation.equals("ban")) {
            banId = blackListUserService.addToBlackList(form, userProfile);
        } else if (operation.equals("unban")) {
            banId = blackListUserService.removeFromBlackList(userProfile);
        }
        Map<String, Object> response = new HashMap<>();
        response.put("id", banId);
        return ResponseEntity.ok(response);
    }
}
