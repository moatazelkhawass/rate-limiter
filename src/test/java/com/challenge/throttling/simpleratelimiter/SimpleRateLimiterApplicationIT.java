package com.challenge.throttling.simpleratelimiter;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class SimpleRateLimiterApplicationIT {

	@Autowired
	private TestRestTemplate restTemplate;

	@Test
	public void testThrottlingScenarios() throws InterruptedException {

		/** Testing configuration is set to limit each user to 5 requests per minute
		 1 - Fire 5 requests using user "user1" should pass
		 2 - 6th request with same user will fail
		 3 - Fire another 5 requests with user "user2" should still pass
		 4 - 6th request of user2 will fail
		 5 - sleep for 1 minute
		 6 - request one more for each user, both should succeed
		 */

		ResponseEntity<String> result;
		for (int i = 0; i < 5; i++) {
			result = restTemplate.withBasicAuth("user1", "password1")
					.getForEntity("/throttledResource", String.class);
			assertEquals(HttpStatus.OK, result.getStatusCode());
		}

		result = restTemplate.withBasicAuth("user1", "password1")
				.getForEntity("/throttledResource", String.class);
		assertEquals(HttpStatus.TOO_MANY_REQUESTS, result.getStatusCode());

		for (int i = 0; i < 5; i++) {
			result = restTemplate.withBasicAuth("user2", "password2")
					.getForEntity("/throttledResource", String.class);
			assertEquals(HttpStatus.OK, result.getStatusCode());
		}

		result = restTemplate.withBasicAuth("user2", "password2")
				.getForEntity("/throttledResource", String.class);
		assertEquals(HttpStatus.TOO_MANY_REQUESTS, result.getStatusCode());

		Thread.sleep(60000);

		result = restTemplate.withBasicAuth("user1", "password1")
				.getForEntity("/throttledResource", String.class);
		assertEquals(HttpStatus.OK, result.getStatusCode());

		result = restTemplate.withBasicAuth("user2", "password2")
				.getForEntity("/throttledResource", String.class);
		assertEquals(HttpStatus.OK, result.getStatusCode());
	}

	@Test
	public void contextLoads() {
		ResponseEntity<String> result = restTemplate.withBasicAuth("test", "test")
				.getForEntity("/throttledResource", String.class);
		assertEquals(HttpStatus.OK, result.getStatusCode());
	}


}
