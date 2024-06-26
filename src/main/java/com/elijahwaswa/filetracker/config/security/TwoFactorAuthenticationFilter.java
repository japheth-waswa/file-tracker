package com.elijahwaswa.filetracker.config.security;

import com.elijahwaswa.filetracker.config.security.CustomUserDetails;
import com.elijahwaswa.filetracker.util.Helpers;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class TwoFactorAuthenticationFilter extends OncePerRequestFilter {

//    private final String twoFactorAuthenticationUrl = Helpers.TWO_FACTOR_AUTHENTICATION_URL;
    private final String twoFactorAuthenticationUrl = Helpers.TWO_FACTOR_AUTHENTICATION_TOTP_URL;
    private final HttpSession session;
    private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
    private final AntPathMatcher pathMatcher = new AntPathMatcher();


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
        if (principal instanceof CustomUserDetails && valid2FA && (request.getServletPath().equalsIgnoreCase(twoFactorAuthenticationUrl) || request.getServletPath().equalsIgnoreCase(Helpers.LOGIN_URL))) {
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
            redirectStrategy.sendRedirect(request, response, twoFactorAuthenticationUrl);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private boolean requires2FAAuthentication(HttpServletRequest request) {
        //don't check for login & 2fa routes
        String path = request.getServletPath();
        boolean allowed  = !path.equalsIgnoreCase(Helpers.LOGIN_URL) &&
                !path.equalsIgnoreCase(Helpers.TWO_FACTOR_AUTHENTICATION_URL) &&
                !path.equalsIgnoreCase(Helpers.TWO_FACTOR_AUTHENTICATION_TOTP_URL);

        for(String publicPath:Helpers.PUBLIC_PATHS){
            allowed = allowed && !pathMatcher.match(publicPath, path);
        }
        return allowed;
    }

    private boolean isValid2FA() {
        Boolean valid2Fa = (Boolean) session.getAttribute(Helpers.TWO_FACTOR_BOOL_FLAG);
        return valid2Fa != null && valid2Fa;
    }
}
