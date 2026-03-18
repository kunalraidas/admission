package com.kunal.admission.config;

import com.kunal.admission.util.JwtUtil;
import io.jsonwebtoken.io.IOException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.hibernate.annotations.processing.Find;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.util.List;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    public JwtAuthFilter(JwtUtil jwtUtil){
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException, java.io.IOException {

        String authHeader = request.getHeader("Authorization");

        if(authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // extract token (remove bearer prefix)
        String token =  authHeader.substring(7);

        // Validate token
        if (!jwtUtil.isTokenValid(token)){
            filterChain.doFilter(request,response);
            return;
        }

        // extract email and role from token
        String email = jwtUtil.extractEmail(token);
        String role = jwtUtil.extractRole(token);

        // Build Spring Security authentication object
        // Spring Security expects roles prefixed with "ROLE_"
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        email,
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_"+ role))
                );

        //Put it in the SecurityContext so Spring Security knows this request is authenticated
        SecurityContextHolder.getContext().setAuthentication(authentication);

        //Continue to the next filter / controller
        filterChain.doFilter(request,response);

    }
}
