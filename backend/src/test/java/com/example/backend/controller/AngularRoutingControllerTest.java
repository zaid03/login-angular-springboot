package com.example.backend.controller;

import com.example.backend.config.TestSecurityConfig;
import com.example.backend.config.TestExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AngularRoutingController.class)
@ActiveProfiles("test")
@Import({TestSecurityConfig.class, TestExceptionHandler.class})
public class AngularRoutingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void redirectRoot_forwardsToIndexHtml() throws Exception {
        mockMvc.perform(get("/"))
            .andDo(print())
            .andExpect(forwardedUrl("/index.html"));
    }

    @Test
    void redirectSimplePath_forwardsToIndexHtml() throws Exception {
        mockMvc.perform(get("/dashboard"))
            .andDo(print())
            .andExpect(forwardedUrl("/index.html"));
    }

    @Test
    void redirectDashboardPath_forwardsToIndexHtml() throws Exception {
        mockMvc.perform(get("/dashboard/overview"))
            .andDo(print())
            .andExpect(forwardedUrl("/index.html"));
    }

    @Test
    void redirectAdminPath_forwardsToIndexHtml() throws Exception {
        mockMvc.perform(get("/admin/users"))
            .andDo(print())
            .andExpect(forwardedUrl("/index.html"));
    }

    @Test
    void redirectComplexPath_forwardsToIndexHtml() throws Exception {
        mockMvc.perform(get("/app/section/page/view"))
            .andDo(print())
            .andExpect(forwardedUrl("/index.html"));
    }

    @Test
    void redirectPathWithHyphen_forwardsToIndexHtml() throws Exception {
        mockMvc.perform(get("/user-profile"))
            .andDo(print())
            .andExpect(forwardedUrl("/index.html"));
    }

    @Test
    void redirectPathWithUnderscore_forwardsToIndexHtml() throws Exception {
        mockMvc.perform(get("/admin_section"))
            .andDo(print())
            .andExpect(forwardedUrl("/index.html"));
    }

    @Test
    void redirectPathWithNumbers_forwardsToIndexHtml() throws Exception {
        mockMvc.perform(get("/section123"))
            .andDo(print())
            .andExpect(forwardedUrl("/index.html"));
    }

    @Test
    void deepPathWithoutDot_forwardsToIndexHtml() throws Exception {
        mockMvc.perform(get("/a/b/c/d/e/f"))
            .andDo(print())
            .andExpect(forwardedUrl("/index.html"));
    }

    @Test
    void pathWithTrailingSlash_forwardsToIndexHtml() throws Exception {
        mockMvc.perform(get("/dashboard/"))
            .andDo(print())
            .andExpect(forwardedUrl("/index.html"));
    }

    @Test
    void pathWithDot_forwardsToIndexHtml() throws Exception {
        mockMvc.perform(get("/api/data.json"))
            .andDo(print())
            .andExpect(forwardedUrl("/index.html"));
    }

    @Test
    void pathWithMultipleDotsAndSlash_forwardsToIndexHtml() throws Exception {
        mockMvc.perform(get("/assets/style.min.css"))
            .andDo(print())
            .andExpect(forwardedUrl("/index.html"));
    }

    @Test
    void pathWithFileExtension_forwardsToIndexHtml() throws Exception {
        mockMvc.perform(get("/script.js"))
            .andDo(print())
            .andExpect(forwardedUrl("/index.html"));
    }

    @Test
    void pathWithImageExtension_forwardsToIndexHtml() throws Exception {
        mockMvc.perform(get("/logo.png"))
            .andDo(print())
            .andExpect(forwardedUrl("/index.html"));
    }
}
