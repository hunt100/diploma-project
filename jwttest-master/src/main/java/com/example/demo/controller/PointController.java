package com.example.demo.controller;

import com.example.demo.data.entity.Point;
import com.example.demo.data.entity.UserProfile;
import com.example.demo.data.enums.PointStatus;
import com.example.demo.data.model.PointDto;
import com.example.demo.service.JwtUserDetailsService;
import com.example.demo.service.PointService;
import com.example.demo.service.UserProfileService;
import com.example.demo.service.UserRatingService;
import com.example.demo.service.mapper.PointMapper;
import com.example.demo.util.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1/api/points")
public class PointController {
    @Autowired
    private PointMapper pointMapper;

    @Autowired
    private PointService pointService;

    @Autowired
    private UserProfileService userProfileService;

    @Autowired
    private JwtUserDetailsService userDetailsService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UserRatingService userRatingService;

    @PostMapping("/add")
    public ResponseEntity<?> createPoint(@RequestBody PointDto pointdto, HttpServletRequest request) {
        pointdto.setPointStatus("CREATED");
        String token = request.getHeader("Authorization");
        token = token.substring(7);
        String username = jwtTokenUtil.getUsernameFromToken(token);
        UserProfile currentUser = userProfileService.findByUser(userDetailsService.findUserByUsername(username));
        Point point = pointMapper.modelToEntity(pointdto);
        point.setUserProfile(currentUser);
        PointDto response = pointMapper.pointToPointDto(pointService.createPoint(point));
        return ResponseEntity.ok(response);
    }

    @PostMapping("/validate")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MODERATOR')")
    public ResponseEntity<?> validateCurrentPoint(@RequestBody PointDto pointDto) {
        return changePointStatus(pointDto, PointStatus.ACTIVE);
    }

    @PostMapping("/cancel")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MODERATOR')")
    public ResponseEntity<?> cancelCurrentPoint(@RequestBody PointDto pointDto) {
        return changePointStatus(pointDto, PointStatus.CANCEL);
    }

    @PostMapping("/resolve")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> resolveCurrentPoint(@RequestBody PointDto pointDto) {
        return changePointStatus(pointDto, PointStatus.RESOLVED);
    }

    @PostMapping("/reopen")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MODERATOR')")
    public ResponseEntity<?> reOpenCurrentPoint(@RequestBody PointDto pointDto) {
        return changePointStatus(pointDto, PointStatus.REOPEN);
    }

    private ResponseEntity<?> changePointStatus(@RequestBody PointDto pointDto, PointStatus status) {
        Point point = pointService.findPointById(pointDto.getId());
        Map<String, String> response = new HashMap<>();
        if (point == null) {
            response.put("error", "point not found");
            response.put("code", "400");
            return ResponseEntity.badRequest().body(response);
        }
        response = pointService.changePointStatus(point, status);
        if (response.get("error") != null) {
            return ResponseEntity.badRequest().body(response);
        } else {
            return ResponseEntity.ok(response);
        }
    }

    @GetMapping
    public ResponseEntity<?> findAllActivePoints () {
        List<PointDto> points = pointService.findAllPointDtoByStatus(PointStatus.ACTIVE);
        return ResponseEntity.ok(pointService.showAllSortedByRating(points));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findPointById(@PathVariable("id") Long id) {
        Point point = pointService.findPointById(id);
        if (point != null) {
            PointDto pointDto = pointMapper.pointToPointDto(point);
            return ResponseEntity.ok(pointService.showAllSortedByRating(Collections.singletonList(pointDto)).get(0));
        }
        return ResponseEntity.ok().build();
    }

    @Deprecated
    @GetMapping("/filter")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MODERATOR')")
    public ResponseEntity<?> findPointsByModerator(@RequestParam(required = false) MultiValueMap<String, String> parameters) {
        return ResponseEntity.ok(pointService.findAllPointsByFilters(parameters));
    }

    @PostMapping("/{id}/like")
    public ResponseEntity<?> putLikeOnPoint(@PathVariable("id") Long pointId, @ApiIgnore HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        token = token.substring(7);
        String username = jwtTokenUtil.getUsernameFromToken(token);
        UserProfile currentUser = userProfileService.findByUser(userDetailsService.findUserByUsername(username));
        boolean likeActive = userRatingService.leaveALike(pointId, currentUser);
        Map<String, Integer> response = new HashMap<>();
        response.put("likeActive", likeActive ? 1 : 0);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/like")
    public ResponseEntity<?> checkIfLikeExist(@PathVariable("id") Long pointId, @ApiIgnore HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        token = token.substring(7);
        String username = jwtTokenUtil.getUsernameFromToken(token);
        UserProfile currentUser = userProfileService.findByUser(userDetailsService.findUserByUsername(username));
        boolean likeActive = userRatingService.isLikeExist(pointId, currentUser);
        Map<String, Integer> response = new HashMap<>();
        response.put("likeActive", likeActive ? 1 : 0);
        return ResponseEntity.ok(response);
    }
}
