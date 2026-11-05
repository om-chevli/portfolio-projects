package com.omchevli.apiplatform;

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
class ApiPlatformControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    void publicListNeverExposesInternalCost() throws Exception {
        mockMvc.perform(get("/api/public/v1/items"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].internalCost").doesNotExist())
                .andExpect(jsonPath("$[0].price").exists());
    }

    @Test
    void internalRequiresApiKey() throws Exception {
        mockMvc.perform(get("/api/internal/v1/items"))
                .andExpect(status().isUnauthorized());
        mockMvc.perform(get("/api/internal/v1/items").header("X-Api-Key", "wrong"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void internalWithKeySeesFullRecord() throws Exception {
        mockMvc.perform(get("/api/internal/v1/items/kb-001").header("X-Api-Key", "dev-internal-key"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.internalCost").value(74.50));
    }

    @Test
    void internalCreateThenPublicRead() throws Exception {
        mockMvc.perform(post("/api/internal/v1/items")
                        .header("X-Api-Key", "dev-internal-key")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":\"wb-010\",\"name\":\"Webcam\",\"category\":\"peripherals\",\"price\":89.99,\"internalCost\":52.00,\"stock\":5}"))
                .andExpect(status().isCreated());
        mockMvc.perform(get("/api/public/v1/items/wb-010"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Webcam"))
                .andExpect(jsonPath("$.internalCost").doesNotExist());
    }

    @Test
    void publicMissingItemIs404() throws Exception {
        mockMvc.perform(get("/api/public/v1/items/nope"))
                .andExpect(status().isNotFound());
    }
}
