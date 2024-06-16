package com.example.swallow.configuration;

import io.github.bucket4j.*;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RateLimitConfig {

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    private Bucket createNewBucket() {
        return Bucket4j.builder()
                .addLimit(Bandwidth.classic(10, Refill.intervally(10, Duration.ofMinutes(1))))
                .build();
    }

    public Bucket resolveBucket(HttpServletRequest request) {
        String ip = request.getRemoteAddr();  // IP 주소를 추출합니다.
        return buckets.computeIfAbsent(ip, k -> createNewBucket());
    }
}
