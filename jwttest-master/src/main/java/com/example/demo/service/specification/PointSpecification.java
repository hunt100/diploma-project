package com.example.demo.service.specification;

import com.example.demo.data.entity.Point;
import com.example.demo.data.enums.PointStatus;
import org.springframework.data.jpa.domain.Specification;

public class PointSpecification {

    public static Specification<Point> firstCall() {
        return ((root, query, criteriaBuilder) -> criteriaBuilder.isNotNull(root.get("id")));
    }

    public static Specification<Point> statusIs(final String pointStatus) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("pointStatus"), PointStatus.valueOf(pointStatus));
    }

    public static  Specification<Point> latEqual(final double lat) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("lat"), lat);
    }

    public static  Specification<Point> lngEqual(final double lng) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("lng"), lng);
    }

    public static  Specification<Point> nameLike(final String name) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get("name"), "%" + name + "%");
    }
}
