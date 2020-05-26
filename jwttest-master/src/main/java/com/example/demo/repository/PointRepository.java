package com.example.demo.repository;

import com.example.demo.data.entity.Point;
import com.example.demo.data.enums.DangerLevel;
import com.example.demo.data.enums.PointStatus;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PointRepository extends BaseRepository<Point>, JpaSpecificationExecutor<Point> {
    Point findByLatAndLng(Double lat, Double lng);

    List<Point> findAllByPointStatus(PointStatus pointStatus);

    Long countAllByPointStatus(PointStatus pointStatuses);

    @Query("SELECT p FROM Point p WHERE (:pointStatus is null or p.pointStatus = :pointStatus) " +
            "and (:dangerLevel is null or p.pointCategory.dangerLevel = :dangerLevel)")
    List<Point> findAllByPointStatusAndPointCategory_DangerLevel(
            @Param("pointStatus") PointStatus pointStatus,
            @Param("dangerLevel") DangerLevel dangerLevel);
}
