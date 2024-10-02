package com.kyonggi.diet.auth.config;

import com.kyonggi.diet.auth.util.JwtTokenUtil;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JwtRequestFilter extends OncePerRequestFilter {

    private JwtTokenUtil jwtTokenUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String authorizationHeader = request.getHeader("Authorization");

        String jwtToken = null;
        String email = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwtToken = authorizationHeader.substring(7);

            try {
                email = jwtTokenUtil.getUsernameFromToken(jwtToken);
            } catch (IllegalStateException ex) {
                throw new RuntimeException("Unable to extract username from token", ex);
            } catch (ExpiredJwtException ex) {
                throw new RuntimeException("Jwt Token is expired", ex);
            }

        }
    }
}
