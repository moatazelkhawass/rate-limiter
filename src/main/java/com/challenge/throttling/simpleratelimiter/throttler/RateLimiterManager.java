package com.challenge.throttling.simpleratelimiter.throttler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class RateLimiterManager {
    private Map<String, RateLimiter> limiters = new ConcurrentHashMap<>();
    private int hourlyLimit;
    private TimeUnit timeUnit;

    public RateLimiterManager(int hourlyLimit, TimeUnit timeUnit) {
        this.hourlyLimit = hourlyLimit;
        this.timeUnit = timeUnit;
    }

    public RateLimiter getRateLimiter(String username) {
        if (limiters.containsKey(username)) {
            return limiters.get(username);
        } else {
            synchronized(username.intern()) {
                if (limiters.containsKey(username)) {
                    return limiters.get(username);
                }

                RateLimiter rateLimiter = RateLimiter.create(hourlyLimit, timeUnit);

                limiters.put(username, rateLimiter);
                return rateLimiter;
            }
        }
    }
}
