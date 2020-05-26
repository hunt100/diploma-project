package com.example.demo.repository;

import com.example.demo.data.entity.NotificationEvent;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationEventRepository extends BaseRepository<NotificationEvent> {
    NotificationEvent findByKeyValue(String keyValue);
}
