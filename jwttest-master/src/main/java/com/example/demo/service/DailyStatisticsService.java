package com.example.demo.service;

import com.example.demo.data.entity.DailyStatistics;
import com.example.demo.data.enums.PointStatus;
import com.example.demo.data.model.DailyStatisticsForm;
import com.example.demo.repository.DailyStatisticsRepository;
import com.example.demo.service.mapper.DailyStatisticsMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class DailyStatisticsService {
    private final DailyStatisticsRepository dailyStatisticsRepository;
    private final DailyStatisticsMapper dailyStatisticsMapper;

    @Autowired
    public DailyStatisticsService(DailyStatisticsRepository dailyStatisticsRepository, DailyStatisticsMapper dailyStatisticsMapper) {
        this.dailyStatisticsRepository = dailyStatisticsRepository;
        this.dailyStatisticsMapper = dailyStatisticsMapper;
    }

    public List<DailyStatisticsForm> getLast30DayStatistics() {
        List<DailyStatisticsForm> forms = new ArrayList<>();
        for (DailyStatistics d : dailyStatisticsRepository.findTop30ByOrderByDateDesc()) {
            forms.add(dailyStatisticsMapper.entityToModel(d));
        }
        return forms;
    }

    @Transactional
    public void updateDayStatistics(PointStatus pointStatus) {
        List<DailyStatistics> statistics = dailyStatisticsRepository.findAllByDate(LocalDate.now());
        log.info("Statistics array {}", statistics.toString());
        DailyStatistics currentDay;
        if (statistics.isEmpty()) {
            currentDay = new DailyStatistics();
            currentDay.setDate(LocalDate.now(ZoneId.of("Asia/Almaty")));
        } else {
            log.info("Get 0 element: {} and time: {}", statistics.get(0).getId(), statistics.get(0).getDate().toString());
            currentDay = statistics.get(0);
        }
        switch (pointStatus) {
            case CREATED:
                currentDay.setCreated(currentDay.getCreated()+1);
                break;
            case ACTIVE:
                currentDay.setActive(currentDay.getActive()+1);
                break;
            case CANCEL:
                currentDay.setCancel(currentDay.getCancel()+1);
                break;
            case RESOLVED:
                currentDay.setResolved(currentDay.getResolved()+1);
                break;
            default: log.error("Unexpected status {}", pointStatus);
        }
        currentDay.setDate(LocalDate.now(ZoneId.of("Asia/Almaty")));
        log.info("Get current stat after set: {} and time: {}", currentDay.getId(), currentDay.getDate().toString());
        dailyStatisticsRepository.save(currentDay);
    }
}
