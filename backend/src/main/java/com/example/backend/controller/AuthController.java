package com.example.backend.controller;

import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.client.RestTemplate;

import org.springframework.web.bind.annotation.CrossOrigin;

import com.example.backend.dto.LoginRequest;
import com.example.backend.sqlserver1.model.User;
import com.example.backend.sqlserver1.repository.UserRepository;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${cas.server-login-url}")
    private String casLoginUrl;

    @Value("${cas.service-url}")
    private String casServiceUrl;

    @GetMapping("/login/cas")
    public RedirectView redirectToCAS() {
        String redirectUrl = casLoginUrl + "?service=" + casServiceUrl;
        System.out.println("Redirecting to CAS: " + redirectUrl);
        return new RedirectView(redirectUrl);
    }

    @GetMapping("/login/cas/callback")
    public ResponseEntity<?> handleCASCallback(@RequestParam(required = false) String ticket) {
        if (ticket != null) {
            // Here you would validate the ticket with CAS server
            // For now, we'll just return success
            System.out.println("Received CAS ticket: " + ticket);
            return ResponseEntity.ok(Map.of(
                "message", "CAS authentication successful",
                "ticket", ticket,
                "redirect", "/dashboard"
            ));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(Map.of("error", "No ticket received from CAS"));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        Optional<User> userOpt = userRepository.findByUSUCOD(loginRequest.getUSUCOD());
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            System.out.println("Login USUCOD: " + loginRequest.getUSUCOD());
            System.out.println("Login USUPASS: " + loginRequest.getUSUPASS());
            System.out.println("DB USUPASS: " + user.getUSUPASS());
            boolean matches = passwordEncoder.matches(loginRequest.getUSUPASS(), user.getUSUPASS());
            System.out.println("Password matches: " + matches);

            if (matches) {
                return ResponseEntity.ok(Map.of("message", "Login successful"));
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(Map.of("error", "Invalid USUCOD or password"));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        System.out.println("USUCOD: " + user.getUSUCOD());
        if (userRepository.findByUSUCOD(user.getUSUCOD()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("USUCOD already in use");
        }
        if (user.getUSUPASS() == null) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Password cannot be null");
        }
        user.setUSUPASS(passwordEncoder.encode(user.getUSUPASS()));
        userRepository.save(user);
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/cas/validate")
    public ResponseEntity<?> validateCASTicket(@RequestBody Map<String, String> request) {
        System.out.println("🎫 Received CAS validation request: " + request);
        String ticket = request.get("ticket");
        String service = request.get("service");
        System.out.println("🎫 Extracted ticket: " + ticket);
        System.out.println("🎫 Extracted service: " + service);
        
        if (ticket == null || service == null) {
            System.out.println("❌ Missing ticket or service");
            return ResponseEntity.badRequest()
                .body(Map.of("success", false, "error", "Missing ticket or service"));
        }
        
        try {
            // Validate ticket with CAS server
            System.out.println("🎫 Validating with CAS server...");
            String username = validateTicketWithCAS(ticket, service);
            System.out.println("🎫 CAS validation result: " + username);
            
            if (username != null) {
                System.out.println("✅ Validation successful for user: " + username);
                return ResponseEntity.ok(Map.of(
                    "success", true,
                    "username", username
                ));
            } else {
                System.out.println("❌ Validation failed - invalid ticket");
                return ResponseEntity.ok(Map.of(
                    "success", false,
                    "error", "Invalid ticket"
                ));
            }
        } catch (Exception e) {
            System.out.println("🚨 Exception during validation: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("success", false, "error", "Validation failed: " + e.getMessage()));
        }
    }

    private String validateTicketWithCAS(String ticket, String service) {
    try {
        // CAS validation URL
        String casValidationUrl = "http://localhost:8081/cas/validate?ticket=" + ticket + "&service=" + service;
        
        // Use RestTemplate to call CAS server
        RestTemplate restTemplate = new RestTemplate();
        String response = restTemplate.getForObject(casValidationUrl, String.class);
        
        // Parse CAS response
        if (response != null && response.startsWith("yes")) {
            // Extract username from response (second line)
            String[] lines = response.split("\n");
            if (lines.length > 1) {
                return lines[1]; // Username is on second line
            }
        }
        return null;
    } catch (Exception e) {
        System.err.println("CAS validation error: " + e.getMessage());
        return null;
    }
}
}
