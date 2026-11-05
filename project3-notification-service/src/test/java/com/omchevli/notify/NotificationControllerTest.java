package com.omchevli.notify;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class NotificationControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    void createAndListWithinTenant() throws Exception {
        mockMvc.perform(post("/api/notifications")
                        .header("X-Tenant-Id", "acme")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\":\"u1\",\"type\":\"TASK\",\"message\":\"Deploy finished\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.tenantId").value("acme"));

        mockMvc.perform(get("/api/notifications").param("userId", "u1").header("X-Tenant-Id", "acme"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].message").value("Deploy finished"));

        // A different tenant sees nothing.
        mockMvc.perform(get("/api/notifications").param("userId", "u1").header("X-Tenant-Id", "globex"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }
}
