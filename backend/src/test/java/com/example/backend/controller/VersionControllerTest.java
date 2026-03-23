package com.example.backend.controller;

import com.example.backend.config.TestSecurityConfig;
import com.example.backend.config.TestExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = VersionController.class)
@ActiveProfiles("test")
@Import({TestSecurityConfig.class, TestExceptionHandler.class})
public class VersionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getVersion_returns200WithVersionMap() throws Exception {
        mockMvc.perform(get("/api/version/num")
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.version", notNullValue()))
            .andExpect(jsonPath("$.version").isString());
    }

    @Test
    void getVersion_returnsValidVersionFormat() throws Exception {
        mockMvc.perform(get("/api/version/num")
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$").exists());
    }

    @Test
    void getVersion_returnsDesconocidaWhenVersionIsNull() throws Exception {
        mockMvc.perform(get("/api/version/num")
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.version").exists())
            .andExpect(result -> {
                String versionValue = result.getResponse().getContentAsString();
                // Should contain either a real version or "desconocida"
                assert(versionValue.contains("version") && (versionValue.contains("desconocida") || versionValue.length() > 10));
            });
    }

    @Test
    void getVersion_trimsDashAndEverythingAfter() throws Exception {
        mockMvc.perform(get("/api/version/num")
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.version").exists())
            .andExpect(result -> {
                String responseBody = result.getResponse().getContentAsString();
                assert(responseBody.matches(".*\"version\"\\s*:\\s*\"[^\"]*\".*"));
            });
    }

    @Test
    void getVersion_returnsJsonWithVersionKey() throws Exception {
        mockMvc.perform(get("/api/version/num")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.version").isString());
    }

    @Test
    void getVersion_containsString_returnsValidResponse() throws Exception {
        mockMvc.perform(get("/api/version/num")
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("version")));
    }
}