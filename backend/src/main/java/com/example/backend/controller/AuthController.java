package com.example.backend.controller;

import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.dto.LoginRequest;
import com.example.backend.sqlserver1.model.User;
import com.example.backend.sqlserver1.repository.UserRepository;

@RestController
@RequestMapping("/api")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

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
}
