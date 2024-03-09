package com.management.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.management.CustomPostgresqlContainer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;


public class UserControllerIntegrationTest extends CustomPostgresqlContainer {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void testCreateUser() throws Exception {
        UserRequest request = new UserRequest();
        // Set user request properties
        request.setFirstname("John");
        request.setLastname("Doe");
        request.setEmail("john.doe@example.com");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void testFindUser() throws Exception {
        User user = User.builder().firstname("John").lastname("Doe").email("john.doe@example.com").build();
        User savedUser = userRepository.save(user);
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/user/{id}", savedUser.getId()))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void testUpdateUser() throws Exception {
        User user = User.builder().firstname("John").lastname("Doe").email("john.doe@example.com").role(Role.ADMIN).build();
        User savedUser = userRepository.save(user);
        UserDTO userDTO = new UserDTO();
        // Set userDTO properties
        userDTO.setId(savedUser.getId());
        userDTO.setFirstname("UpdatedFirstName");
        userDTO.setLastname("UpdatedLastName");
        userDTO.setEmail("john.doe@example.com");
        userDTO.setRole(Role.USER.name());

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO))
                        .with(SecurityMockMvcRequestPostProcessors.user(user)))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void testDeleteUser() throws Exception {
        User user = User.builder().firstname("John").lastname("Doe").email("john.doe@example.com").build();
        User savedUser = userRepository.save(user);
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/user/{id}", savedUser.getId()))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void testFindUsers() throws Exception {
        User user = User.builder().firstname("John").lastname("Doe").email("john.doe@example.com").role(Role.ADMIN).build();
        User savedUser = userRepository.save(user);
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/user")
                        .param("firstname", savedUser.getFirstname())
                        .param("lastname", savedUser.getLastname())
                        .param("ids", String.valueOf(savedUser.getId()))
                        .param("role", savedUser.getRole().name())
                .with(SecurityMockMvcRequestPostProcessors.user(user)))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
}

