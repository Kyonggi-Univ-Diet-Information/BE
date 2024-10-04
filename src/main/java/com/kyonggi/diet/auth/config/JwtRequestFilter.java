package com.kyonggi.diet.auth.config;

import com.kyonggi.diet.auth.util.JwtTokenUtil;
import com.kyonggi.diet.member.service.CustomMembersDetailService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JwtRequestFilter extends OncePerRequestFilter {

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection") // I have to remove this and fix the error
    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection") // I have to remove this and fix the error
    @Autowired
    private CustomMembersDetailService membersDetailService;

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

        if(email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = membersDetailService.loadUserByUsername(email);

            if(jwtTokenUtil.validateToken(jwtToken, userDetails)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        filterChain.doFilter(request, response);
    }
}
