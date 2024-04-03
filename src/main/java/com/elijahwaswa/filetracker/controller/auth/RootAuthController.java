package com.elijahwaswa.filetracker.controller.auth;

import com.elijahwaswa.filetracker.dto.UserDto;
import com.elijahwaswa.filetracker.service.EmailService;
import com.elijahwaswa.filetracker.service.user.UserService;
import com.elijahwaswa.filetracker.util.Helpers;
import com.elijahwaswa.filetracker.util.ResetLinkPayload;
import com.elijahwaswa.filetracker.util.TwoFactorPayload;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

record UserToken(boolean status, UserDto userDto) {
}

@Controller
public class RootAuthController {
    private final UserService userService;
    private final HttpServletRequest request;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final Logger LOGGER = LoggerFactory.getLogger(RootAuthController.class);

    @Value("${spring.application.name}")
    private String appName;

    public RootAuthController(UserService userService, HttpServletRequest request, EmailService emailService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.request = request;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping(Helpers.LOGIN_URL)
    public String login() {
        return Helpers.LOGIN_PAGE_VIEW;
    }

    @GetMapping(Helpers.RESET_PASSWORD_URL)
    public String resetPasswordPage() {
        return Helpers.RESET_PASSWORD_PAGE_VIEW;
    }

    @PostMapping(Helpers.RESET_PASSWORD_URL)
    public String resetPassword(@RequestParam String idNumber) {
        if (idNumber == null || idNumber.isBlank()) return "redirect:" + Helpers.RESET_PASSWORD_URL + "?error=true";

        UserDto userDto;
        String  resetLink;
        try {
            ResetLinkPayload resetLinkPayload = userService.generatePasswordResetLink(idNumber, Helpers.generateBaseUrl(request));
            userDto = resetLinkPayload.userDto();
            resetLink = resetLinkPayload.resetLink();
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return "redirect:" + Helpers.RESET_PASSWORD_URL + "?error=true";
        }

        //email service in new thread to send email with reset link
        UserDto finalUserDto = userDto;
        executorService.execute(() -> emailService.sendSimpleMessage(finalUserDto.getEmail(), "Reset Password", "Click on the link to reset your password: " + resetLink));

        return "redirect:" + Helpers.RESET_PASSWORD_URL + "?success=true";
    }

    private UserToken validateUserToken(String token) {
        if (token == null || token.isBlank()) return new UserToken(false, null);
        //fetch the user
        UserDto userDto;
        try {
            userDto = userService.fetchUserByResetPasswordToken(token);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return new UserToken(false, null);
        }

        //check if resetPasswordExpiryTime > now
        Instant resetPasswordExpiryTime = userDto.getResetPasswordExpiryTime();
        if (resetPasswordExpiryTime != null && resetPasswordExpiryTime.isAfter(Instant.now())) {
            //success
            return new UserToken(true, userDto);
        }
        return new UserToken(false, null);
    }

    @GetMapping(Helpers.NEW_PASSWORD_URL)
    public String newPasswordPage(Model model, @RequestParam String token) {
        //ensure token is valid
        UserToken userToken = validateUserToken(token);
        if (!userToken.status()) return "redirect:" + Helpers.LOGIN_URL;

        try {
            TwoFactorPayload twoFactorPayload = userService.generateToTpQRCodeBase64Encoded(userToken.userDto().getIdNumber(), appName);
            model.addAttribute("qrCodeBase64", twoFactorPayload.qrCodeBase64());
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return "redirect:" + Helpers.LOGOUT_URL;
        }

        return Helpers.NEW_PASSWORD_PAGE_VIEW;
    }

    @PostMapping(Helpers.NEW_PASSWORD_URL)
    public String newPassword(@RequestParam String password, @RequestParam String confirmPassword, @RequestParam String token) {

        //ensure the passwords match & length is not less than 6
        password = password != null ? password.trim() : "";
        confirmPassword = confirmPassword != null ? confirmPassword.trim() : "";

        if (password.length() < 6 || !password.equals(confirmPassword))
            return "redirect:" + Helpers.NEW_PASSWORD_URL + "?error=password must match and be atleast 6 characters long&token=" + token;

        //ensure token is valid
        UserToken userToken = validateUserToken(token);
        if (!userToken.status()) return "redirect:" + Helpers.LOGIN_URL;

        //clear token
        UserDto userDto = userToken.userDto();
        userDto.setPassword(passwordEncoder.encode(password));
        userDto.setResetPasswordToken(UUID.randomUUID().toString());
        userService.updateUser(userDto);
        return "redirect:" + Helpers.LOGIN_URL;
    }
}
