package com.challenge.throttling.simpleratelimiter.throttler;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class RateLimiter {
    private Semaphore semaphore;
    private int maxPermits;
    private TimeUnit timePeriod;
    private ScheduledExecutorService scheduler;

    private LocalDateTime initializationTime;

    private static final int SECONDS_PER_MINUTE = 60;
    private static final int MINUTES_PER_HOUR = 60;

    public static RateLimiter create(int permits, TimeUnit timePeriod) {
        RateLimiter limiter = new RateLimiter(permits, timePeriod);
        limiter.schedulePermitReload();
        limiter.initializationTime = LocalDateTime.now();

        return limiter;
    }

    private RateLimiter(int permits, TimeUnit timePeriod) {
        this.semaphore = new Semaphore(permits);
        this.maxPermits = permits;
        this.timePeriod = timePeriod;
    }

    public int tryAcquire() {
        boolean acquire = semaphore.tryAcquire();
        int remainingTime = -1;

        if(!acquire) {
            LocalDateTime timeNow = LocalDateTime.now();

            if (timePeriod == TimeUnit.HOURS) {
                remainingTime = MINUTES_PER_HOUR - (timeNow.getMinute()- initializationTime.getMinute());
            }
            else {
                remainingTime = SECONDS_PER_MINUTE - (timeNow.getSecond()- initializationTime.getSecond());
            }
        }

        return remainingTime;
    }

    private void schedulePermitReload() {
        scheduler = Executors.newScheduledThreadPool(1);
        scheduler.schedule(this::run, 1, timePeriod);
    }

    private void run() {
        semaphore.release(maxPermits - semaphore.availablePermits());
    }
}
