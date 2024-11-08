package com.bsmlabs.resilience4j;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class UserInfoService {

    private final RestTemplate restTemplate;

    public UserInfoService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<BenchProfiles> getBenchProfilesInfo() {
        return restTemplate.exchange("http://localhost:8083/api/non-secure/benchprofiles/fetch-users",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<BenchProfiles>>() {}
        ).getBody();
    }
}
