package com.example.spring_boot_mongodb_redis.controller;

import com.example.spring_boot_mongodb_redis.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserControllerTest {

    private static Long testUserId;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @Order(1)
    void shouldListAllUsers() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThan(0))));
    }

    @Test
    @Order(2)
    void shouldCreateUser() throws Exception {
        User user = User.builder()
                .firstName("John")
                .lastName("Doe")
                .maidenName("Smith")
                .gender("male")
                .email("john.doe@example.com")
                .phone("+1234567890")
                .username("johndoe")
                .password("password123")
                .birthDate(LocalDate.of(1990, 1, 1))
                .build();

        String response = mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.username").value("johndoe"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        testUserId = objectMapper.readTree(response).get("id").asLong();
    }

    @Test
    @Order(3)
    void shouldGetUserById() throws Exception {
        mockMvc.perform(get("/api/users/" + testUserId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testUserId));
    }

    @Test
    @Order(4)
    void shouldUpdateUser() throws Exception {
        User updated = User.builder()
                .id(testUserId)
                .firstName("Updated John")
                .lastName("Doe Updated")
                .maidenName("Smith Smith")
                .gender("male male")
                .email("john.updated@example.com")
                .phone("+0987654321")
                .username("johnny")
                .password("newpassword")
                .birthDate(LocalDate.of(1991, 11, 11))
                .build();

        mockMvc.perform(put("/api/users/" + testUserId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("johnny"))
                .andExpect(jsonPath("$.email").value("john.updated@example.com"));
    }

    @Test
    @Order(5)
    void shouldDeleteUser() throws Exception {
        mockMvc.perform(delete("/api/users/" + testUserId))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/users/" + testUserId))
                .andExpect(status().isNotFound());
    }
}
