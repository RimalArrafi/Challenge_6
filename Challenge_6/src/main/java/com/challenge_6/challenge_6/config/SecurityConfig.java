package com.challenge_6.challenge_6.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Autowired
    JWTAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests()
                .requestMatchers("/v1/auth/**")
                .permitAll()
                .requestMatchers(HttpMethod.GET, "/v1/users")
                .hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/v1/orders/**")
                .hasRole("ADMIN")
                .requestMatchers("/v1/users/**")
                .authenticated()
                .requestMatchers(HttpMethod.GET, "/v1/merchants", "/v1/products/**")
                .authenticated()
                .requestMatchers("/v1/products/**")
                .hasRole("MERCHANT")
                .requestMatchers(HttpMethod.PUT, "/v1/merchants/**")
                .hasRole("MERCHANT")
                .requestMatchers(HttpMethod.DELETE, "/v1/merchants/**")
                .hasRole("MERCHANT")
                .requestMatchers(HttpMethod.POST, "/v1/merchants")
                .hasRole("CUSTOMER")
                .requestMatchers(HttpMethod.GET, "/v1/merchants")
                .hasRole("CUSTOMER")
                .requestMatchers(HttpMethod.GET, "/v1/orders/**")
                .hasAnyRole("CUSTOMER", "ADMIN")
                .requestMatchers("/v1/orders/**", "/v1/report")
                .hasRole("CUSTOMER")
                .anyRequest()
                .permitAll()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
