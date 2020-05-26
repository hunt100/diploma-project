package com.example.demo.data.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@AllArgsConstructor
public class PageModelForm<T extends BaseForm> {
    @NotNull(message = "currentPage не может быть null-ом")
    private Integer currentPage;
    @NotNull(message = "currentSize не может быть null-ом")
    private Integer currentSize;
    private int totalPages;
    private long totalSize;
    private List<T> content;
}
