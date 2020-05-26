package com.example.demo.service.mapper;

import com.example.demo.data.entity.Point;
import com.example.demo.data.model.PointDto;
import com.example.demo.service.PointService;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring",
        uses = PointService.class,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface PointMapper extends BaseMapper<Point, PointDto>{

    @Mapping(source = "pointCategory.iconName", target = "iconName")
    @Mapping(source = "pointCategory.iconUrl", target = "iconUrl")
    @Mapping(source = "pointCategory.dangerLevel", target = "dangerLevel")
    @Mapping(source = "userProfile.id", target = "userProfileDto.id")
    @Mapping(source = "userProfile.firstName", target = "userProfileDto.firstName")
    @Mapping(source = "userProfile.lastName", target = "userProfileDto.lastName")
    @Mapping(source = "userProfile.patronymic", target = "userProfileDto.patronymic")
    @Mapping(source = "userRate", target = "userRating")
    @Mapping(source = "userProfile.initialsAvatar", target = "userProfileDto.initialsAvatar")
    PointDto pointToPointDto (Point point);

    default PointDto pointToPointDtoOnlyCoordinates(Point point) {
        if (point == null) {
            return null;
        }
        PointDto pointDto = new PointDto();
        pointDto.setId(point.getId());
        pointDto.setName(point.getName());
        pointDto.setLat(point.getLat());
        pointDto.setLng(point.getLng());
        return pointDto;
    }

    @Mapping(target = "pointCategory.iconName", source = "iconName")
    @Mapping(target = "pointCategory.iconUrl", source = "iconUrl")
    @Mapping(target = "pointCategory.dangerLevel", source = "dangerLevel")
    Point modelToEntity(PointDto pointDto);

}
