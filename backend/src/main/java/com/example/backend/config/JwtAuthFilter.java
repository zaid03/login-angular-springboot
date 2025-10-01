package com.example.backend.config;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwt;
    private static final List<String> PUBLIC_PREFIXES = List.of(
        "/api/login",
        "/api/cas",
        "/api/filter",
        "/api/validate-usucod",
        "/health",
        "/api/sical",
        "/api/rpc/call"
    );

    public JwtAuthFilter(JwtUtil jwt){ this.jwt = jwt; }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
    throws ServletException, IOException {

    String path = req.getRequestURI();
    System.out.println("JwtAuthFilter: Processing path=" + path + " method=" + req.getMethod());

    if ("OPTIONS".equalsIgnoreCase(req.getMethod())) {
        res.setStatus(HttpServletResponse.SC_OK);
        chain.doFilter(req, res);
        return;
    }

    if (isPublic(path)) {
        System.out.println("JwtAuthFilter: Public path, skipping auth for " + path);
        chain.doFilter(req, res);
        return;
    }

    System.out.println("JwtAuthFilter: Protected path, checking JWT for " + path);

        String auth = req.getHeader(HttpHeaders.AUTHORIZATION);
        if (auth == null || !auth.startsWith("Bearer ")) {
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String token = auth.substring(7);
        try {
            String user = jwt.validateAndGetSubject(token);
            if (user == null) {
                res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
            System.out.println("JwtAuthFilter OK user=" + user + " path=" + path);
            UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(user, null, java.util.List.of());
            SecurityContextHolder.getContext().setAuthentication(authentication);
            chain.doFilter(req, res);
        } catch (Exception e) {
            SecurityContextHolder.clearContext();
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }

    private boolean isPublic(String path) {
        return PUBLIC_PREFIXES.stream().anyMatch(path::startsWith);
    }
}