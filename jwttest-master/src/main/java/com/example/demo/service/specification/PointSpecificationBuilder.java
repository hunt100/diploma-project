package com.example.demo.service.specification;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class PointSpecificationBuilder {

    public enum DataType {
        POINT_STATUS("pointStatus"),
        LAT("lat"),
        LNG("lng"),
        NAME("name");

        private String fieldName;

        DataType(String fieldName) {
            this.fieldName = fieldName;
        }

        public String getFieldName() {
            return fieldName;
        }

    }

    public Specification build(MultiValueMap<String, String> params) {
        if (params.size() == 0) {
            return null;
        }
        Specification result = PointSpecification.firstCall();
        List<SearchCriteria> criteriaList = params.entrySet().stream().flatMap(me -> me.getValue().stream().map(e -> new SearchCriteria(me.getKey(), e))).collect(Collectors.toList());
        int statusCounter = 0;
        for (SearchCriteria s : criteriaList) {
            DataType dataType = analyze(s.getKey());
            if (dataType == null) continue;
            switch (dataType) {
                case POINT_STATUS:
                    result = statusCounter > 0 ? result.or(PointSpecification.statusIs(s.getValue().toString())) : result.and(PointSpecification.statusIs(s.getValue().toString()));
                    statusCounter++;
                    break;
                case LAT: result = result.and(PointSpecification.latEqual((Double.parseDouble(s.getValue().toString())))); break;
                case LNG: result = result.and(PointSpecification.lngEqual((Double.parseDouble(s.getValue().toString())))); break;
                case NAME: result = result.and(PointSpecification.nameLike(s.getValue().toString())); break;
            }
        }
        return result;
    }

    private DataType analyze(String fieldName) {
        if (Arrays.stream(DataType.values()).noneMatch(s -> s.getFieldName().equals(fieldName))) {
            log.warn("There are no such type as: ", fieldName);
            return null;
        } else {
            return Arrays.stream(DataType.values()).filter(s -> s.getFieldName().equals(fieldName)).findFirst().get();
        }
    }
}
