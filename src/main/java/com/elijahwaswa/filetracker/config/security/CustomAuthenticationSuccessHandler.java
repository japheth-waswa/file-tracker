package com.elijahwaswa.filetracker.config.security;

import com.elijahwaswa.filetracker.service.EmailService;
import com.elijahwaswa.filetracker.util.Helpers;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private  final EmailService emailService;
    private final ExecutorService executorService= Executors.newSingleThreadExecutor();

    public CustomAuthenticationSuccessHandler(EmailService emailService) {
        this.emailService = emailService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        request.getSession().setAttribute("department", userDetails.getDepartment());
        request.getSession().setAttribute("fullNames", userDetails.getFullNames());
        response.setStatus(HttpServletResponse.SC_OK);

        //Generate a 2FA code
        String twoFactorCode = generateTwoFactorCode();
        userDetails.setTwoFactorCode(twoFactorCode);

        //send the 2FA code to the user's email in a new thread
//        executorService.execute(()->emailService.sendSimpleMessage(userDetails.getDepartment(), "Your 2FA Code", "Your 2FA code is: " + twoFactorCode));

        response.sendRedirect(Helpers.TWO_FACTOR_AUTHENTICATION_TOTP_URL);
//        response.sendRedirect(Helpers.TWO_FACTOR_AUTHENTICATION_URL);
    }

    private String generateTwoFactorCode(){
        return String.format("%06d",new Random().nextInt(1000000)); //Generate a 6-digit code
    }
}
