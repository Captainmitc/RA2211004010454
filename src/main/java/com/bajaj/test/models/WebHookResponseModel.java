package com.bajaj.test.models;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WebHookResponseModel {

    private String webhook;
    private String accessToken;
    private Wrapper data;
}
