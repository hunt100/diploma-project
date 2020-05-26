package com.example.demo.controller;

import com.example.demo.data.model.DailyStatisticsForm;
import com.example.demo.service.DailyStatisticsService;
import com.example.demo.service.PointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1/api/stats")
public class StatisticsController {
    private final PointService pointService;
    private final DailyStatisticsService dailyStatisticsService;

    @Autowired
    public StatisticsController(PointService pointService, DailyStatisticsService dailyStatisticsService) {
        this.pointService = pointService;
        this.dailyStatisticsService = dailyStatisticsService;
    }

    @GetMapping
    public ResponseEntity<Map<String, Long>> getStatistics () {
        return ResponseEntity.ok(pointService.getAllPointStatistics());
    }

    @GetMapping("/monthly")
    public ResponseEntity<List<DailyStatisticsForm>> getMonthlyStatistics() {
        return ResponseEntity.ok(dailyStatisticsService.getLast30DayStatistics());
    }
}
