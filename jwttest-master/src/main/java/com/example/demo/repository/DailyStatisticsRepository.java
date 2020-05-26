package com.example.demo.repository;

import com.example.demo.data.entity.DailyStatistics;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DailyStatisticsRepository extends BaseRepository<DailyStatistics> {

    List<DailyStatistics> findTop30ByOrderByDateDesc();

    List<DailyStatistics> findAllByDate(LocalDate date);
}
