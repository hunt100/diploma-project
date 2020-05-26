package com.example.demo.service.mapper;

import com.example.demo.data.entity.BaseEntity;
import com.example.demo.data.model.BaseForm;

public interface BaseMapper<E extends BaseEntity, F extends BaseForm> {
    E modelToEntity(F form);
    F entityToModel(E entity);
}
