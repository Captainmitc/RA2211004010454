package com.bajaj.test.models;


import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class User {

    private int id;
    private String name;
    private List<Integer> follows;
}
