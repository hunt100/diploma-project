package com.example.demo.service;

import com.example.demo.data.entity.Point;
import com.example.demo.data.enums.PointStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
public class CronService {
    private final PointService pointService;

    @Autowired
    public CronService(PointService pointService) {
        this.pointService = pointService;
    }

    @Deprecated
    public void updateValidateStatusCron () {
      log.info("updateValidateStatusCron start work at: {}", LocalDateTime.now());
       for (Point point : pointService.findAllPointsByStatus(PointStatus.VALIDATED)) {
           pointService.changePointStatus(point, PointStatus.ACTIVE);
       }
       log.info("updateValidateStatusCron stop work at: {}", LocalDateTime.now());
    }
}
