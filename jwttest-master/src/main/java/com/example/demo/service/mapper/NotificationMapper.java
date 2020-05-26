package com.example.demo.service.mapper;

import com.example.demo.data.entity.Notification;
import com.example.demo.data.model.NotificationForm;
import com.example.demo.service.PointService;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring",
        uses = PointService.class,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface NotificationMapper extends BaseMapper<Notification, NotificationForm>{

    @Mapping(source = "point.pointStatus", target = "pointStatus")
    @Mapping(source = "point.id", target = "pointId")
    @Mapping(source = "point.name", target = "pointName")
    NotificationForm entityToModel(Notification notification);
}
