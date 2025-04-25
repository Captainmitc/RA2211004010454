package com.bajaj.test.service;


import com.bajaj.test.models.TestResponse;
import com.bajaj.test.models.User;
import com.bajaj.test.models.WebHookResponseModel;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.bajaj.test.utils.Constants.REG_NO;

@Service
public class TestService {

    public TestResponse solveProblem(WebHookResponseModel model) {

        if(model != null && model.getData() != null && model.getAccessToken() != null && model.getWebhook() != null) {
            List<User> users = model.getData().getUsers();
            Map<Integer, Set<Integer>> followMap = new HashMap<>();

            for (User user : users) {
                followMap.put(user.getId(), new HashSet<>(user.getFollows()));
            }

            Set<String> seen = new HashSet<>();
            List<List<Integer>> result = new ArrayList<>();

            for (User user : users) {
                int userId = user.getId();
                for (int followedId : user.getFollows()) {
                    if (followMap.containsKey(followedId) && followMap.get(followedId).contains(userId)) {
                        int min = Math.min(userId, followedId);
                        int max = Math.max(userId, followedId);
                        String pairKey = min + ":" + max;
                        if (!seen.contains(pairKey)) {
                            result.add(Arrays.asList(min, max));
                            seen.add(pairKey);
                        }
                    }
                }
            }
            return TestResponse.builder()
                    .result(result)
                    .regNo(REG_NO)
                    .build();
        }
        return null;
    }
}
