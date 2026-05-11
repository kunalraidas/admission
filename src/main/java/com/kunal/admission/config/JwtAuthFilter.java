package com.kunal.admission.config;

import com.kunal.admission.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    public JwtAuthFilter(JwtUtil jwtUtil){
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        // No Authorization header -> let request through as anonymous.
        // Spring Security's authorization rules will reject it if the endpoint is protected.
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Extract token (remove "Bearer " prefix)
        String token = authHeader.substring(7);

        // Validate token. If a token *is* presented but is invalid/expired,
        // reject the request explicitly with 401 — don't silently fall through.
        if (!jwtUtil.isTokenValid(token)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"message\":\"Invalid or expired token. Please login again.\"}");
            return;
        }

        // Extract email and role from token
        String email = jwtUtil.extractEmail(token);
        String role = jwtUtil.extractRole(token);

        // Build Spring Security authentication object.
        // Spring Security expects roles prefixed with "ROLE_".
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        email,
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_" + role))
                );

        // Put it in the SecurityContext so Spring Security knows this request is authenticated
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Continue to the next filter / controller
        filterChain.doFilter(request, response);
    }
}
