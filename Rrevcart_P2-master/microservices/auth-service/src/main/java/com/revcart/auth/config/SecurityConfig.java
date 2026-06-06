package com.revcart.auth.config;

import com.revcart.auth.security.CustomUserDetailsService;
import com.revcart.auth.security.OAuth2LoginSuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
    
    @Bean
    @Order(1)
    public SecurityFilterChain apiSecurityFilterChain(HttpSecurity http) throws Exception {
        http.securityMatcher(new AntPathRequestMatcher("/auth/**"))
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(authz -> authz.anyRequest().permitAll())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .httpBasic(httpBasic -> httpBasic.disable())
            .formLogin(form -> form.disable());
        return http.build();
    }
    
    @Bean
    @Order(2)
    public SecurityFilterChain oauth2SecurityFilterChain(HttpSecurity http, OAuth2LoginSuccessHandler successHandler) throws Exception {
        http.securityMatcher("/oauth2/**", "/login/oauth2/**")
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(authz -> authz.anyRequest().permitAll())
            .oauth2Login(oauth2 -> oauth2
                .loginProcessingUrl("/login/oauth2/code/*")
                .successHandler(successHandler)
            );
        return http.build();
    }
    
    @Bean
    @Order(3)
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/actuator/**").permitAll()
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html", "/api-docs/**").permitAll()
                .anyRequest().permitAll()
            );
        return http.build();
    }
}