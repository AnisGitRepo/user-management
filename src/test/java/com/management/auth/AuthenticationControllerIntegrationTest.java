package com.management.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.management.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

public class AuthenticationControllerIntegrationTest extends IntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AuthenticationService authenticationService;

    @Test
    public void testRegister() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setFirstname("testUser");
        registerRequest.setLastname("testUser");
        registerRequest.setEmail("testUser@mail.com");
        registerRequest.setPassword("testPassword");
        registerRequest.setConfirmPassword("testPassword");

        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testLogin() throws Exception {
        AuthenticationRequest authenticationRequest = new AuthenticationRequest();
        authenticationRequest.setEmail("testUser@mail.com");
        authenticationRequest.setPassword("testPassword");

        authenticationService.register(new RegisterRequest("testUser", "testUser", "testUser@mail.com", "testPassword", "testPassword"));

        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authenticationRequest)));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testResetPassword() throws Exception {
        ChangePasswordRequest changePasswordRequest = ChangePasswordRequest.builder()
                .email("testUser@mail.com")
                .currentPassword("testPassword")
                .newPassword("newTestPassword")
                .confirmationPassword("newTestPassword")
                .build();

        authenticationService.register(new RegisterRequest("testUser", "testUser", "testUser@mail.com", "testPassword", "testPassword"));

        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/auth/reset-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(changePasswordRequest)));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }
}

