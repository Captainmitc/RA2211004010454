package com.bajaj.test.service;


import com.bajaj.test.models.TestResponse;
import com.bajaj.test.models.WebHookResponseModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

import static com.bajaj.test.utils.Constants.*;

@Service
@Slf4j
public class WebService {

    @Value("${webhook.url}")
    private  String url;

    @Value("${testWebHook.url}")
    private String testWebHookUrl;

    @Autowired
    TestService testService;

    @Autowired
    RestTemplate restTemplate;

    private static final int MAX_ATTEMPTS = 4;

    public void executeExternalApi() {
        int attempt = 1;
        while (attempt <= MAX_ATTEMPTS) {
            try {
                Map<String, String> body = new HashMap<>();
                body.put("name", MY_NAME);
                body.put("regNo", REG_NO);
                body.put("email", EMAIL);

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);

                ResponseEntity<WebHookResponseModel> response = restTemplate.postForEntity(
                        url,
                        request,
                        WebHookResponseModel.class
                );

                if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                    WebHookResponseModel model = response.getBody();
                    TestResponse testResponse = testService.solveProblem(model);
                    this.executeTestWebHook(testResponse, model);
                    return; // Success, exit the method
                } else {
                    log.warn("Attempt {}: Failed with status {}", attempt, response.getStatusCode());
                }

            } catch (Exception e) {
                log.error("Attempt {}: Failed to execute generate webhook: {}", attempt, e.getMessage(), e);
            }

            attempt++;
            if (attempt <= MAX_ATTEMPTS) {
                try {
                    Thread.sleep(1000); // Optional backoff
                } catch (InterruptedException ignored) {}
            } else {
                log.error("All attempts to call generateWebhook API failed.");
            }
        }
    }


    public void executeTestWebHook(TestResponse testResponse, WebHookResponseModel model) {
        int attempt = 1;
        while (attempt<=MAX_ATTEMPTS) {
            try {
                if (testResponse != null) {
                    HttpHeaders headers = new HttpHeaders();
                    headers.add("Authorization", model.getAccessToken());
                    headers.setContentType(MediaType.APPLICATION_JSON);
                    HttpEntity<TestResponse> request = new HttpEntity<>(testResponse, headers);
                    ResponseEntity<Object> response = restTemplate.postForEntity(testWebHookUrl, request, Object.class);
                    if (response.getStatusCode() == HttpStatusCode.valueOf(200)) {
                        System.out.println(response.getBody());
                        return;
                    }
                }
                attempt++;
                try {
                    Thread.sleep(1000); // Optional: 1 second backoff between retries
                } catch (InterruptedException ignored) {}
            }
            catch (Exception e) {
                log.error("failed to execute test web hook : {}", e.getMessage(), e);
            }
            if (attempt > MAX_ATTEMPTS) {
                System.err.println("All attempts to post to webhook failed.");
            }
        }
    }

}
