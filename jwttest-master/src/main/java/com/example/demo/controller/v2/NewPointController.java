package com.example.demo.controller.v2;

import com.example.demo.data.model.PointDto;
import com.example.demo.data.model.PointFilterForm;
import com.example.demo.service.PointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("v2/api/points")
public class NewPointController {
    private final PointService pointService;

    @Autowired
    public NewPointController(PointService pointService) {
        this.pointService = pointService;
    }

    @GetMapping("/filter")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MODERATOR')")
    public ResponseEntity<List<PointDto>> filterPointsForModerator(
            @RequestParam(required = false) String pointStatus,
            @RequestParam(required = false) String dangerLevel) {
        PointFilterForm filterForm = new PointFilterForm();
        filterForm.setPointStatus(pointStatus);
        filterForm.setDangerLevel(dangerLevel);
        List<PointDto> points = pointService.findAllByFilterV2(filterForm);
        return ResponseEntity.ok(pointService.showAllSortedByRating(points));
    }

}
