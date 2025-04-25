package com.bajaj.test.config;

import com.bajaj.test.service.WebService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;


@Component
public class StartUp implements CommandLineRunner {

    @Autowired
    WebService webService;


    @Override
    public void run(String... args) throws Exception {
        webService.executeExternalApi();
    }
}
