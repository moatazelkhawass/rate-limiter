package com.challenge.throttling.simpleratelimiter.controllers;

import com.challenge.throttling.simpleratelimiter.throttler.RateLimiter;
import com.challenge.throttling.simpleratelimiter.throttler.RateLimiterManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;

@RestController
public class ThrottlingController {
    @Value("${throttling.limit}")
    private int throttlingLimitPerPeriod;

    @Value("${throttling.time.unit}")
    private String throttlingTimeUnit;

    private final Logger logger = LoggerFactory.getLogger(ThrottlingController.class);
    private RateLimiterManager processor;
    private TimeUnit processorTimeUnit;

    @PostConstruct
    public void init() {
        setProcessorTimeUnit(throttlingTimeUnit);
        processor = new RateLimiterManager(throttlingLimitPerPeriod, processorTimeUnit);
    }

    @GetMapping("/throttledResource")
    public ResponseEntity<String> resourceThrottling(){

        String username = getUserNameFromContext();

        logger.info(String.format("Invocation to throttledResource by logged in user: %s", username));

        RateLimiter rateLimiter = processor.getRateLimiter(username);
        int remainingTime = rateLimiter.tryAcquire();
        if(remainingTime != -1){
            String timeUnit = getTimeUnit();
            String errorMessage = String.format("Rate Limit exceeded. Try again in %s %s", remainingTime, timeUnit);
            throw new HttpClientErrorException(HttpStatus.TOO_MANY_REQUESTS, errorMessage);
        }

        return ResponseEntity.ok().body("ok");
    }

    private void setProcessorTimeUnit(String throttlingTimeUnit) {

        if(throttlingTimeUnit.equalsIgnoreCase("hour") ||
                throttlingTimeUnit.equalsIgnoreCase("h"))
            processorTimeUnit = TimeUnit.HOURS;
        else if (throttlingTimeUnit.equalsIgnoreCase("minute") ||
                throttlingTimeUnit.equalsIgnoreCase("m")) {
            processorTimeUnit = TimeUnit.MINUTES;
        }
        else {
            throw new RuntimeException("Invalid time unit in configurations");
        }
    }

    private String getUserNameFromContext() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username;
        if (principal instanceof UserDetails) {
            username = ((UserDetails)principal).getUsername();
        } else {
            username = principal.toString();
        }

        return username;
    }

    private String getTimeUnit() {
        String timeUnit;
        if (processorTimeUnit == TimeUnit.HOURS){
            timeUnit = "minutes";
        }
        else{
            timeUnit = "seconds";
        }

        return timeUnit;
    }
}
