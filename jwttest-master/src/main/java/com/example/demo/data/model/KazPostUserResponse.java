package com.example.demo.data.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data
public class KazPostUserResponse {
    private String name;
    private boolean resident;
    private boolean juridic;

    public List<String> divideFullNameBySpaces() {
        return new ArrayList<>(Arrays.asList(this.name.split(" ")));
    }
}
