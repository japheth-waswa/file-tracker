package com.elijahwaswa.filetracker.config;

import com.elijahwaswa.filetracker.util.Helpers;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class TwoFactorAuthenticationFilter extends OncePerRequestFilter {
    private final HttpSession session;
    private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    public TwoFactorAuthenticationFilter(HttpSession session) {
        this.session = session;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Authentication authentication=SecurityContextHolder.getContext().getAuthentication();
        if(authentication == null){
            filterChain.doFilter(request, response);
            return;
        }

        Object principal = authentication.getPrincipal();
        boolean valid2FA = isValid2FA();

        // If user is already authenticated and request is for /2fa, login etc, redirect to /authenticated route
        if (principal instanceof CustomUserDetails && valid2FA && (request.getServletPath().equalsIgnoreCase(Helpers.TWO_FACTOR_AUTHENTICATION_URL) || request.getServletPath().equalsIgnoreCase(Helpers.LOGIN_URL))) {
            redirectStrategy.sendRedirect(request, response, Helpers.AUTHENTICATED_ROOT_URL);
            return;
        }

        //if 2FA check not allowed
        if (!requires2FAAuthentication(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        //check if 2FA was valid
        if (principal instanceof CustomUserDetails && !valid2FA) {
            redirectStrategy.sendRedirect(request, response, Helpers.TWO_FACTOR_AUTHENTICATION_URL);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private boolean requires2FAAuthentication(HttpServletRequest request) {
        //don't check for login & 2fa routes
        return !request.getServletPath().equalsIgnoreCase(Helpers.LOGIN_URL) && !request.getServletPath().equalsIgnoreCase(Helpers.TWO_FACTOR_AUTHENTICATION_URL);
    }

    private boolean isValid2FA() {
        Boolean valid2Fa = (Boolean) session.getAttribute(Helpers.TWO_FACTOR_BOOL_FLAG);
        return valid2Fa != null && valid2Fa;
    }
}
