package com.omchevli.docengine;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class DocumentControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    void unauthenticatedIsRejected() throws Exception {
        mockMvc.perform(get("/api/documents"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void editorCanCreateAndList() throws Exception {
        mockMvc.perform(post("/api/documents")
                        .with(SecurityMockMvcRequestPostProcessors.httpBasic("om", "om-password"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"API notes\",\"content\":\"REST design notes.\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("API notes"))
                .andExpect(jsonPath("$.owner").value("om"));

        mockMvc.perform(get("/api/documents")
                        .with(SecurityMockMvcRequestPostProcessors.httpBasic("om", "om-password")))
                .andExpect(status().isOk());
    }

    @Test
    void viewerCannotCreate() throws Exception {
        mockMvc.perform(post("/api/documents")
                        .with(SecurityMockMvcRequestPostProcessors.httpBasic("guest", "guest-password"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Nope\",\"content\":\"Should fail.\"}"))
                .andExpect(status().isForbidden());
    }
}
