package com.example.demo.service;

import com.example.demo.data.entity.Point;
import com.example.demo.data.enums.DangerLevel;
import com.example.demo.data.enums.PointStatus;
import com.example.demo.data.model.PointDto;
import com.example.demo.data.model.PointFilterForm;
import com.example.demo.data.model.UserRatingForm;
import com.example.demo.repository.PointRepository;
import com.example.demo.service.mapper.PointMapper;
import com.example.demo.service.specification.PointSpecificationBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PointService {

    @Autowired
    private PointRepository pointRepository;

    @Autowired
    private PointMapper pointMapper;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private PointSpecificationBuilder specificationBuilder;

    @Autowired
    private DailyStatisticsService dailyStatisticsService;

    public List<Point> findAll() {
        return pointRepository.findAll();
    }

    public Point createPoint(PointDto pointdto) {
        Point point = pointMapper.modelToEntity(pointdto);
        return pointRepository.save(point);
    }

    public Point createPoint(Point point) {
        dailyStatisticsService.updateDayStatistics(PointStatus.CREATED);
        return pointRepository.save(point);
    }

    public Point findPointById(Long id) {
        Optional<Point> point = pointRepository.findById(id);
        if (!point.isPresent()) {
            log.warn("Not founded user with Id: {}", id);
            throw new IllegalArgumentException("id - " + id);
        }
        return point.get();
    }

    public void deletePoint(Long id) {
        pointRepository.deleteById(id);
    }

    public void updatePoint(Long id, Point point) {
        Optional<Point> foundedPoint = pointRepository.findById(id);
        if (foundedPoint.isPresent()) {
            point.setId(id);
            pointRepository.save(point);
        } else {
            log.warn("Not founded user with Id: {}", id);
            throw new IllegalArgumentException("Illegal UserProfile id -" + point.getId());
        }
    }

    public PointDto entityToModel(Point point) {
        return pointMapper.pointToPointDto(point);
    }

    public List<PointDto> getAllPointsDtoWithOnlyCoordinates(List<Point> points) {
        if (points == null || points.isEmpty()) {
            return Collections.emptyList();
        }
        List<PointDto> dtoList = new ArrayList<>();
        for (Point point : points) {
            dtoList.add(pointMapper.pointToPointDtoOnlyCoordinates(point));
        }
        return dtoList;
    }

    public Point findPointByLatAndLng(Double lat, Double lng) {
        return pointRepository.findByLatAndLng(lat, lng);
    }

    public Map<String, String> changePointStatus(Point current, PointStatus nextStatus) {
        Map<String, String> result = new HashMap<>();
        if (current.getPointStatus().isFinished()) {
            if (nextStatus.equals(PointStatus.REOPEN)) {
                return reOpenPoint(current);
            }
            log.warn("Current point already in finish status: {}, code :{}", current.getPointStatus().name(), current.getPointStatus().getStatusCode());
            result.put("error", "Current status already finished, can't transit to another");
            throw new IllegalStateException("Current status already finished, can't transit to another");
        } else if (current.getPointStatus().getStatusCode() >= nextStatus.getStatusCode()) {
            log.warn("Trying to switch on wrong status. From {} to {}", current.getPointStatus().name(), nextStatus.name());
            result.put("error", "Wrong argument for next status");
            throw new IllegalArgumentException("Wrong argument for next status");
        } else {
            current.setPointStatus(nextStatus);
            dailyStatisticsService.updateDayStatistics(current.getPointStatus());
            updatePoint(current.getId(), current);
            result.put("id", String.valueOf(current.getId()));
            result.put("status", nextStatus.name());
            notificationService.createNotification(current, nextStatus);
        }
        return result;
    }

    private Map<String, String> reOpenPoint(Point point) {
        Map<String, String> result = new HashMap<>();
        log.info("reOpen logic appear! Point: {} become ACTIVE again!", point.getId());
        point.setPointStatus(PointStatus.ACTIVE);
        updatePoint(point.getId(), point);
        result.put("id", String.valueOf(point.getId()));
        result.put("status", point.getPointStatus().name());
        dailyStatisticsService.updateDayStatistics(PointStatus.ACTIVE);
        return result;
    }

    public List<PointDto> findAllPointDtoByStatus(PointStatus pointStatus) {
        List<PointDto> points = new ArrayList<>();
        for (Point point : pointRepository.findAllByPointStatus(pointStatus)) {
            points.add(pointMapper.pointToPointDto(point));
        }
        return points;
    }

    public List<Point> findAllPointsByStatus(PointStatus pointStatus) {
        return pointRepository.findAllByPointStatus(pointStatus);
    }

    public List<PointDto> findAllPointsByFilters(MultiValueMap<String, String> parameters) {
        Specification specification = specificationBuilder.build(parameters);
        List<Point> points;
        if (specification == null) {
            points = pointRepository.findAll();
        } else {
            points = pointRepository.findAll(specification);
        }
        return points.stream().map(point -> pointMapper.pointToPointDto(point)).collect(Collectors.toList());
    }

    public Map<String, Long> getAllPointStatistics() {
        Map<String, Long> statuses = new HashMap<>();
        Long allPointCounts = 0L;
        for (PointStatus status : PointStatus.values()) {
            statuses.put(status.name(), pointRepository.countAllByPointStatus(status));
            allPointCounts += statuses.get(status.name());
        }
        statuses.put("ALL", allPointCounts);
        return statuses;
    }

    public List<PointDto> findAllByFilterV2(@NotNull PointFilterForm filterForm) {
        List<PointDto> pointDtos = new ArrayList<>();
        List<Point> points;
        if (filterForm.getDangerLevel() == null && filterForm.getPointStatus() == null) {
             points = pointRepository.findAll();
        }else {
            points = pointRepository.findAllByPointStatusAndPointCategory_DangerLevel(
                    filterForm.getPointStatus() == null ? null : PointStatus.valueOf(filterForm.getPointStatus()),
                    filterForm.getDangerLevel() == null ? null : DangerLevel.valueOf(filterForm.getDangerLevel()));
        }
        for (Point point : points) {
            pointDtos.add(entityToModel(point));
        }
        return pointDtos;
    }

    public List<PointDto> showAllSortedByRating(List<PointDto> points) {
        for (PointDto p : points) {
            p.setRating(p.getUserRating().stream().filter(UserRatingForm::isRate).count());
        }
        points.sort(Comparator.comparing(PointDto::getRating).reversed());
        return points;
    }
}
