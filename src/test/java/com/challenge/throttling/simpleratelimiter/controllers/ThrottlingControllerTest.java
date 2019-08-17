package com.challenge.throttling.simpleratelimiter.controllers;

import com.challenge.throttling.simpleratelimiter.throttler.RateLimiterManager;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(ThrottlingController.class)
public class ThrottlingControllerTest {
    @Autowired
    private MockMvc mvc;

    @WithMockUser(value = "user1")
    @Test
    public void whenGetRequestWithAuthUser_shouldSucceedWith200() throws Exception {
        mvc.perform(get("/throttledResource").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void whenGetRequestWithNoUser_shouldFailWith401() throws Exception {
        mvc.perform(get("/throttledResource").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }
}
