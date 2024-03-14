package com.elijahwaswa.filetracker.config;

import com.elijahwaswa.filetracker.service.EmailService;
import com.elijahwaswa.filetracker.util.UserRole;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.expression.WebExpressionAuthorizationManager;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@AllArgsConstructor
public class SecurityConfig {

    private EmailService emailService;

    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http,TwoFactorAuthenticationFilter twoFactorAuthenticationFilter) throws Exception {
        http
                .addFilterBefore(twoFactorAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
//                                .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()
                                .requestMatchers("/hello/su").hasRole(UserRole.SU.name())
                                .requestMatchers("/hello/admin").hasRole(UserRole.ADMIN.name())
                                .requestMatchers("/hello/user").hasRole(UserRole.USER.name())
                                .requestMatchers("/hello/user-supervisor").access(
                                        new WebExpressionAuthorizationManager(
                                                "hasRole(T(com.elijahwaswa.filetracker.util.UserRole).SU.name()) or (hasRole(T(com.elijahwaswa.filetracker.util.UserRole).USER.name()) and hasAuthority(T(com.elijahwaswa.filetracker.util.UserRight).SUPERVISOR.name()))"
                                        )
                                )
                                .anyRequest().authenticated())
                .formLogin(customizer -> customizer
                        .successHandler(new CustomAuthenticationSuccessHandler(emailService))
                )
                .csrf(csrf->csrf.disable());
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
