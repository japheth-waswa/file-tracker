package com.elijahwaswa.filetracker.controller.auth;

import com.elijahwaswa.filetracker.dto.UserDto;
import com.elijahwaswa.filetracker.service.EmailService;
import com.elijahwaswa.filetracker.service.user.UserService;
import com.elijahwaswa.filetracker.util.Helpers;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
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

        //fetch user by id number
        UserDto userDto;
        try {
            userDto = userService.fetchUser(idNumber);
        } catch (Exception e) {
            return "redirect:" + Helpers.RESET_PASSWORD_URL + "?error=true";
        }

        //resetPasswordExpiryTime
        Instant resetPasswordExpiryTime = Instant.now().plus(Duration.ofMinutes(Helpers.RESET_PASSWORD_EXPIRY_MINUTES));

        //resetPasswordToken
        String resetPasswordToken = UUID.randomUUID().toString().replaceAll("-", "") + UUID.randomUUID().toString().replaceAll("-", "");
        String resetLink = Helpers.generateBaseUrl(request) + Helpers.NEW_PASSWORD_URL + "?token=" + resetPasswordToken;

        //email service in new thread to send email with reset link
        executorService.execute(() -> emailService.sendSimpleMessage(userDto.getEmail(), "Reset Password", "Click on the link to reset your password: " + resetLink));

        //update user
        userDto.setResetPasswordExpiryTime(resetPasswordExpiryTime);
        userDto.setResetPasswordToken(resetPasswordToken);
        userService.updateUser(userDto);
        return "redirect:" + Helpers.RESET_PASSWORD_URL + "?success=true";
    }

    private UserToken validateUserToken(String token) {
        if (token == null || token.isBlank()) return new UserToken(false, null);
        //fetch the user
        UserDto userDto;
        try {
            userDto = userService.fetchUserByResetPasswordToken(token);
        } catch (Exception e) {
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
    public String newPasswordPage(@RequestParam String token) {
        //ensure token is valid
        if (!validateUserToken(token).status()) return "redirect:" + Helpers.LOGIN_URL;
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
