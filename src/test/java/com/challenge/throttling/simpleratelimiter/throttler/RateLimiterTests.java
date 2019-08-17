package com.challenge.throttling.simpleratelimiter.throttler;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

@RunWith(SpringRunner.class)
public class RateLimiterTests {

    @Test
    public void tryAcquire_whenNotExceedsLimits_ShouldReturnMinusOne() {
        int limits = 3;
        TimeUnit timeUnit = TimeUnit.MINUTES;
        RateLimiter rateLimiter = RateLimiter.create(limits, timeUnit);

        int timeRemaining = rateLimiter.tryAcquire();
        assertEquals(-1, timeRemaining);
    }

    @Test
    public void tryAcquire_whenExceedsLimits_ShouldReturnPositiveNumber() {
        int limits = 3;
        TimeUnit timeUnit = TimeUnit.MINUTES;
        RateLimiter rateLimiter = RateLimiter.create(limits, timeUnit);

        int timeRemaining = -1;

        for (int i = 0; i < 4; i++) {
            timeRemaining = rateLimiter.tryAcquire();
        }

        assertNotEquals(-1, timeRemaining);
    }

    @Test
    public void tryAcquire_whenExceedsLimitsThenWait_ShouldReturnNegativeOne() throws InterruptedException {
        int limits = 3;
        TimeUnit timeUnit = TimeUnit.MINUTES;
        RateLimiter rateLimiter = RateLimiter.create(limits, timeUnit);

        int timeRemaining;

        for (int i = 0; i < 4; i++) {
            timeRemaining = rateLimiter.tryAcquire();
        }

        Thread.sleep(90000);
        timeRemaining = rateLimiter.tryAcquire();
        assertEquals(-1, timeRemaining);
    }
}
