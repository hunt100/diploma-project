package com.example.demo.service.specification;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SearchCriteria {
    private String key;
    private String operation;
    private Object value;

    public SearchCriteria(String key, Object value) {
        this.key = key;
        this.value = value;
        this.operation=":";
    }
}
