package com.example.backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/version")
public class VersionController {
    @GetMapping("/num")
    public Map<String, String> getVersion() {
        String version = getClass().getPackage().getImplementationVersion();
        return Map.of("version", version != null ? version : "unknown");
    }
}
