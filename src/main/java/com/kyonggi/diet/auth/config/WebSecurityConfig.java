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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

@Configuration
public class WebSecurityConfig {

    @Autowired
    private CustomMembersDetailService customMembersDetailService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity, HandlerMappingIntrospector introspector) throws Exception {
        return httpSecurity
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(new MvcRequestMatcher(introspector, "/api/review/favorite/{type}/{id}")).permitAll()
                        //.requestMatchers("swagger-ui/**", "/v3/api-docs/**").denyAll()
                        .requestMatchers(
                                // Basic API
                                "/health", "/", "/actuator/health",

                                // Auth API
                                "/api/login", "api/register", "/api/kakao-form",
                                "/api/kakao-login/**", "/api/google-form", "/api/google-login/**", "/api/oauth2/google/**",
                                 "/auth", "/api/apple-login","/api/apple-form",
                                // Swagger API
                                "swagger-ui/**", "/v3/api-docs/**",

                                // Food API
                                "/api/food/*/*", "/api/food/KYONGSUL/restaurant/*", "api/food/*/get-names/*",
                                "/api/read-csv/*", "/api/food/*/each-category", "/api/food/top5-menu",
                                "/api/food/*/category/*", "/api/food/*/get-sets/*", "/api/food/*/sets-one/*",

                                // Review API
                                "/api/review/*/reviews/top5-rating","/api/review/*/reviews/top5-recent",
                                "/api/review/*/count/*","/api/review/*/rating-count/*",
                                "/api/review/*/average/*","/api/review/*/one/*", "/api/review/*/paged/*",
                                "/api/review/*/all/*", "/api/review/top5-recent",
                                "/api/review/favorite/*/count/*",

                                // Diet Content API
                                "/api/diet-content/dormitory/dow/*", "api/diet-content/dormitory",

                                // Search API
                                "/api/search/**",

                                // Withdraw API
                                "/api/withdraw/etcs", "/api/withdraw/reasons", "/api/withdraw/each-count"
                                 ).permitAll().anyRequest().authenticated())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class)
                .httpBasic(Customizer.withDefaults())
                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // Apple OAuth 콜백 대응
        config.addAllowedOriginPattern("*");

        config.addAllowedMethod("*");
        config.addAllowedHeader("*");

        // Apple은 쿠키 안 씀 → false
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
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
