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
    @RateLimiter(name = "userDetailsService", fallbackMethod = "rateLimitFallback")
    @TimeLimiter(name = "userDetailsService", fallbackMethod = "timeLimitFallback")
    @CircuitBreaker(name = "userDetailsService", fallbackMethod = "fallbackResponse")
    @Retry(name = "userDetailsService", fallbackMethod = "retryFallback")
    public List<BenchProfiles> getBenchProfiles() {
        return userInfoService.getBenchProfilesInfo();
    }

    public CompletableFuture<List<BenchProfiles>> rateLimitFallback(Exception e) {
        logger.error("Rate limit exceeded. Reason: {}", e.getMessage());
        return CompletableFuture.completedFuture(
                List.of(new BenchProfiles("Rate Limited", "Too many requests"))
        );
    }

    public CompletableFuture<List<BenchProfiles>> timeLimitFallback(Exception e) {
        logger.error("Time limit exceeded. Reason: {}", e.getMessage());
        return CompletableFuture.completedFuture(
                List.of(new BenchProfiles("Timeout Error", "Operation took too long to complete"))
        );
    }

    public List<String> fallbackResponse(Exception e) {
        System.err.println("Circuit Breaker triggered. Reason: " + e.getMessage());
        return List.of("Service unavailable, please try again later");
    }

    public List<BenchProfiles> retryFallback(Exception e) {
        logger.error("All retry attempts failed. Reason: {}", e.getMessage());
        return List.of(new BenchProfiles("Default Profile", "Fallback data after retry attempts exhausted"));
    }
}
