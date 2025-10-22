package com.kyonggi.diet.auth.config;

import com.kyonggi.diet.member.service.CustomMembersDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

@Configuration
public class WebSecurityConfig {

    @Autowired
    private CustomMembersDetailService customMembersDetailService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity, HandlerMappingIntrospector introspector) throws Exception {
        return httpSecurity.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(new MvcRequestMatcher(introspector, "/api/review/favorite/{type}/{id}")).permitAll()
                        //.requestMatchers("swagger-ui/**", "/v3/api-docs/**").denyAll()
                        .requestMatchers(
                                // Basic API
                                "/health", "/", "/actuator/health",

                                // Auth API
                                "/api/login", "api/register", "/api/kakao-form",
                                "/api/kakao-login/**",

                                // Swagger API
                                "swagger-ui/**", "/v3/api-docs/**",

                                // Food API
                                "/api/food/*/*", "/api/food/KYONGSUL/restaurant/*", "api/food/*/get-names/*",
                                "/api/read-csv/*",

                                // Review API
                                "/api/review/*/reviews/top5-rating","/api/review/*/reviews/top5-recent",
                                "/api/review/*/count/*","/api/review/*/rating-count/*",
                                "/api/review/*/average/*","/api/review/*/one/*", "/api/review/*/paged/*",
                                "/api/review/*/all/*",
                                "/api/review/favorite/*/count/*",

                                // Diet Content API
                                "/api/diet-content/dormitory/dow/*", "api/diet-content/dormitory"

                                 ).permitAll().anyRequest().authenticated())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class)
                .httpBasic(Customizer.withDefaults())
                .build();
    }

    @Bean
    public JwtRequestFilter authenticationJwtTokenFilter() {
        return new JwtRequestFilter();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public DaoAuthenticationProvider authProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(customMembersDetailService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {return new BCryptPasswordEncoder();}
}
