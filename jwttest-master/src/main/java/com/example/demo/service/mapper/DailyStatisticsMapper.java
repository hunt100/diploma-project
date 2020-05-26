package com.example.demo.service.mapper;

import com.example.demo.data.entity.DailyStatistics;
import com.example.demo.data.model.DailyStatisticsForm;
import com.example.demo.service.DailyStatisticsService;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring",
        uses = DailyStatisticsService.class,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface DailyStatisticsMapper extends BaseMapper<DailyStatistics, DailyStatisticsForm>{
}
