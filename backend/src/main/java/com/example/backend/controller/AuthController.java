package com.example.backend.controller;

import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.util.UriComponentsBuilder;
import com.example.backend.config.JwtUtil;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api")
public class AuthController {

    private final JwtUtil jwtUtil;
    public AuthController(JwtUtil jwtUtil){ this.jwtUtil = jwtUtil; }

    @Value("${cas.server-login-url}")
    private String casLoginUrl;

    @Value("${cas.service-url}")
    private String casServiceUrl;

    @GetMapping("/login/cas")
    public RedirectView redirectToCAS() {
        String redirectUrl = UriComponentsBuilder.fromHttpUrl(casLoginUrl)
                .queryParam("service", casServiceUrl)
                .build(true).toUriString();
        return new RedirectView(redirectUrl);
    }

    @GetMapping("/login/cas/callback")
    public ResponseEntity<?> handleCASCallback(@RequestParam(required = false) String ticket) {
        if (ticket != null) {
            return ResponseEntity.ok(Map.of(
                "message", "CAS authentication successful",
                "ticket", ticket,
                "redirect", "/dashboard"
            ));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(Map.of("error", "No ticket received from CAS"));
    }

    @PostMapping("/cas/validate")
    public ResponseEntity<?> validateCASTicket(@RequestBody Map<String, String> request) {
        String ticket = request.get("ticket");
        String service = request.get("service");
        if (ticket == null || service == null) {
            return ResponseEntity.badRequest()
                .body(Map.of("success", false, "error", "Missing ticket or service"));
        }
        try {
            String username = validateTicketWithCAS(ticket, service);
            if (username != null) {
                String token = jwtUtil.generate(username);
                return ResponseEntity.ok(Map.of(
                    "success", true,
                    "username", username,
                    "token", token
                ));
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("success", false, "error", "Invalid ticket"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("success", false, "error", "Validation failed: " + e.getMessage()));
        }
    }

    private String validateTicketWithCAS(String ticket, String service) {
        try {
            String url = UriComponentsBuilder
                .fromHttpUrl("http://localhost:8081/cas/validate")
                .queryParam("ticket", ticket)
                .queryParam("service", service)
                .build(true).toUriString();
            RestTemplate rest = new RestTemplate();
            String resp = rest.getForObject(url, String.class);
            if (resp != null && resp.startsWith("yes")) {
                String[] lines = resp.split("\n");
                if (lines.length > 1) return lines[1];
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }
}