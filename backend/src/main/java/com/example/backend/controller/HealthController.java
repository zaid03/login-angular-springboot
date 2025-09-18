package com.example.backend.controller;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/health")
public class HealthController {

    @PersistenceContext(unitName = "sqlserver1")
    private EntityManager em1;

    @PersistenceContext(unitName = "sqlserver2")
    private EntityManager em2;

    @GetMapping("/db")
    public Map<String, Object> db() {
        Map<String, Object> out = new HashMap<>();
        out.put("time", Instant.now().toString());
        out.put("sqlserver1", check(em1));
        out.put("sqlserver2", check(em2));
        return out;
    }

    private Map<String, Object> check(EntityManager em) {
        Map<String, Object> r = new HashMap<>();
        try {
            Object v = em.createNativeQuery("SELECT 1").getSingleResult();
            r.put("ok", true);
            r.put("result", v);
        } catch (Exception e) {
            r.put("ok", false);
            r.put("error", e.getClass().getSimpleName() + ": " + e.getMessage());
        }
        return r;
    }
}
