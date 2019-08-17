**Airtasker Challenge**

_Objective:_

This application is a rate-limiter module that stops a particular requestor from making too many http requests within a particular period of time.

_Solution:_

The module is implemented in Java and the test application is a Spring boot application. The solution meant to be fairly simple so that it uses mainly the standard Java libraries with no depencies on other libraries. and meant to be contained only in plain Java classes, so if necessary it could be packaged into other applications.

* Package `throttler`:
    * `RateLimiter`:
        * It utilizes Java `concurrent` package to handle rate-limiting using `Semaphore` and `ScheduledExecutorService`
        * During instantiation an object of this class, the scheduler is set to work within the configured period of time and the semaphore is initiated with the maximum number of requests allowed.
        * With each attempt to call the API, the method `tryAcquire` is called to check if the number of calls have exceeded the limit within the given period of time set within the scheduler.
        * If `tryAcquire` returns false, the amount of time needed to release the Semaphore is calculated and returned to the client. So that it would be clear when the client would be able to try again.
        * The public method create is provided so that it takes care of the object instantiation, starting the scheduler and set the initial time.
    
    * `RateLimiterManager`
        * Since it is required to make the rate-limiter specific to a user, this class takes care to manage rate limiters, so that it assigns a rate limiter per logged-in user.
        * It includes a `ConcurrentHashMap` of (Username and RateLimiter) to keep track of each username rate-limiter.
        * With each attempt to call the API, the method `getRateLimiter` is invoked to check if there is a RateLimiter created for this user or it creates a new one for this user.
    
* `ThrottlingController`
    * This resource exposed a GET API to demonstrate the rate-limiting technique. Security module is included to use very simple Basic Authentication to create three users: user1, user2 and test.
    * The purpose of the security module to keep track of the logged in user.
    * The flow is very simple as follows:
        * Gets the username
        * Log statement
        * Get the rate-limiter for this user
        * Try acquiring user's rate-limiter
        * If acquiring succeeded, it proceeded (in this case just returns "ok")
        * If acquiring failed, it throws an error with the appropriate error message and error code   

* Logging:
    * Logging is only introduced in the `ThrottlingController` API and logging is taken away from the utility classes. 
    * `logback.xml` configures the logs to be in the logs directory with the name `rate_limiter_challenge.log`
    
* Testing:
    * There are seven test cases included:
        * `ThrottlingControllerTests`: testing happy and worst scenarios for invoking ThrottlingController
        * `RateLimiterTests`: 
            * Testing acquiring the rate limiter once, which should pass
            * Acquiring rate limiter more that configured limit with a minute, which should fail
            * Acquiring rate limiter more that configured limit with a minute then wait for a minute and try again, this should succeed.
        * `SimpleRateLimiterApplicationIT`:
            * This one tests whole application end-to-end as follows:
                * Hit a request from user1.. expected to succeed
                * Hit more than permitted requests from user1.. expected to fail
                * Hit a request from user2.. expected to succeed
                * Hit more than permitted requests from user2.. expected to fail   
                * Wait for one minute
                * Hit one more request from user1 and user 2. Expected to succeed.
* Configurations:
    * Configurations are a`pplication.properties` file. Just to configure two items:
        * `throttling.time.unit`: The unit of time required to have the limit within. This is configured to `HOUR` in the application and to `MINUTE` in test.
        * `throttling.limit`: The maximum number of requests permitted per the period of time configured above. This is configured in the application to `100` and to `5` in test.
* Deployment:
    * Check in the code locally in any local folder (LOCAL_FOLDER)
    * To build, run the maven command: `%LOCAL_FOLDER%/mvn clean install`
    * To run: `%LOCAL_FOLDER%/java -jar target/simpleratelimiter-0.0.1-SNAPSHOT.jar`