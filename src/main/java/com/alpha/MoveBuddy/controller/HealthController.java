package com.alpha.MoveBuddy.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
public class HealthController {

    @GetMapping("/health")
    public Map<String, String> healthCheck() {
        return Map.of(
            "status", "UP",
            "message", "MoveBuddy Backend is awake 🚀",
            "timestamp", String.valueOf(System.currentTimeMillis())
        );
    }
}
