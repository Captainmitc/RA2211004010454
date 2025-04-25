package com.bajaj.test.models;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class TestResponse {

    private String regNo;

    private List<List<Integer>> result;
}
