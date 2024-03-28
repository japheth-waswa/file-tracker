package com.elijahwaswa.filetracker.config.security;

import com.elijahwaswa.filetracker.service.EmailService;
import com.elijahwaswa.filetracker.util.Helpers;
import com.elijahwaswa.filetracker.util.UserRole;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.expression.WebExpressionAuthorizationManager;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.spring6.view.ThymeleafViewResolver;

@Configuration
@AllArgsConstructor
public class Config {
    private EmailService emailService;

    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http, TwoFactorAuthenticationFilter twoFactorAuthenticationFilter) throws Exception {
        http
                .addFilterBefore(twoFactorAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(authorizeRequests ->
                        {
                            for (String path : Helpers.PUBLIC_PATHS) {
                                authorizeRequests.requestMatchers(path).permitAll();
                            }
                            authorizeRequests
                                    .requestMatchers("/settings/**").hasAnyRole(UserRole.SU.name())
                                    .requestMatchers("/users/**").hasAnyRole(UserRole.SU.name(), UserRole.ADMIN.name())
                                    .requestMatchers("/departments/**").hasAnyRole(UserRole.SU.name(), UserRole.ADMIN.name())
                                    .requestMatchers("/files/delete").hasAnyRole(UserRole.SU.name())
                                    .requestMatchers("files/manage").access(
                                            new WebExpressionAuthorizationManager(
                                                    "hasRole(T(com.elijahwaswa.filetracker.util.UserRole).SU.name()) or hasRole(T(com.elijahwaswa.filetracker.util.UserRole).ADMIN.name()) or (hasRole(T(com.elijahwaswa.filetracker.util.UserRole).USER.name()) and hasAuthority(T(com.elijahwaswa.filetracker.util.UserRight).SUPERVISOR.name()))"
                                            )
                                    )
                                    .requestMatchers("/files/add-file_trail").hasAnyRole(UserRole.SU.name(), UserRole.ADMIN.name())
//                                    .requestMatchers("/files/**").hasAnyRole(UserRole.SU.name(), UserRole.ADMIN.name(), UserRole.USER.name())

                                    .requestMatchers("/hello/su").hasRole(UserRole.SU.name())
                                    .requestMatchers("/hello/admin").hasRole(UserRole.ADMIN.name())
                                    .requestMatchers("/hello/user").hasRole(UserRole.USER.name())
                                    .requestMatchers("/hello/user-supervisor").access(
                                            new WebExpressionAuthorizationManager(
                                                    "hasRole(T(com.elijahwaswa.filetracker.util.UserRole).SU.name()) or (hasRole(T(com.elijahwaswa.filetracker.util.UserRole).USER.name()) and hasAuthority(T(com.elijahwaswa.filetracker.util.UserRight).SUPERVISOR.name()))"
                                            )
                                    )
                                    .anyRequest().authenticated();
                        }
                )
                .formLogin(customizer -> customizer
                        .successHandler(new CustomAuthenticationSuccessHandler(emailService))
                        .loginPage(Helpers.LOGIN_URL)
                        .failureUrl(Helpers.LOGIN_URL + "?error=true")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutSuccessUrl(Helpers.LOGIN_URL + "?logout=true")
                        .permitAll()
                )
                .csrf(AbstractHttpConfigurer::disable);
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public ViewResolver thymeleafViewResolver(SpringTemplateEngine templateEngine) {
        ThymeleafViewResolver viewResolver = new ThymeleafViewResolver();
        viewResolver.setTemplateEngine(templateEngine);
        viewResolver.setOrder(0); // Set order to 0 so it has higher precedence than JSP
        viewResolver.setViewNames(new String[]{Helpers.LOGIN_PAGE_VIEW, Helpers.RESET_PASSWORD_PAGE_VIEW, Helpers.NEW_PASSWORD_PAGE_VIEW}); // Only resolve specific views with Thymeleaf
        return viewResolver;
    }
}
