package com.bsmlabs.resilience4j;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UserInfoController {

    private final UserInfoService userInfoService;

    public UserInfoController(UserInfoService userInfoService) {
        this.userInfoService = userInfoService;
    }

    @GetMapping("/benchprofiles")
    @CircuitBreaker(name="benchProfileService", fallbackMethod = "fallbackResponse")
    public List<BenchProfiles> getBenchProfiles() {
        return userInfoService.getBenchProfilesInfo();
    }

    public List<String> fallbackResponse(Exception e) {
        System.err.println("Circuit Breaker triggered. Reason: " + e.getMessage());
        return List.of("Service unavailable, please try again later");
    }
}
