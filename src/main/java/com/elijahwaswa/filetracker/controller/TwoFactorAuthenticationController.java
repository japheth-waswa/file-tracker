package com.elijahwaswa.filetracker.controller;

import com.elijahwaswa.filetracker.config.CustomUserDetails;
import com.elijahwaswa.filetracker.model.User;
import com.elijahwaswa.filetracker.repository.UserRepository;
import com.elijahwaswa.filetracker.util.Helpers;
import dev.samstevens.totp.code.CodeVerifier;
import dev.samstevens.totp.code.HashingAlgorithm;
import dev.samstevens.totp.exceptions.QrGenerationException;
import dev.samstevens.totp.qr.QrData;
import dev.samstevens.totp.qr.QrDataFactory;
import dev.samstevens.totp.qr.QrGenerator;
import dev.samstevens.totp.secret.SecretGenerator;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.Duration;
import java.time.Instant;
import java.util.Base64;

@Controller
public class TwoFactorAuthenticationController {

    private final HttpSession session;
    private final SecretGenerator secretGenerator;
    private final QrDataFactory qrDataFactory;
    private final QrGenerator qrGenerator;
    private final CodeVerifier codeVerifier;
    private final UserRepository userRepository;


    @Value("${spring.application.name}")
    private String appName;

    public TwoFactorAuthenticationController(HttpSession session, SecretGenerator secretGenerator, QrDataFactory qrDataFactory, QrGenerator qrGenerator, CodeVerifier codeVerifier, UserRepository userRepository) {
        this.session = session;
        this.secretGenerator = secretGenerator;
        this.qrDataFactory = qrDataFactory;
        this.qrGenerator = qrGenerator;
        this.codeVerifier = codeVerifier;
        this.userRepository = userRepository;
    }

    @GetMapping(Helpers.TWO_FACTOR_AUTHENTICATION_TOTP_URL)
    public String showSetup2FA(Model model) {
        //get user record
        TwoFactorUserDetails twoFactorUserDetails = getTwoFactorUserDetails();
        if (twoFactorUserDetails == null) return "redirect:" + Helpers.LOGOUT_URL;

        //re-use secret if it exists
        String dbSecret = twoFactorUserDetails.user().getTwoFactorSecret();
        String secret = dbSecret != null ? dbSecret : secretGenerator.generate();
        QrData data = qrDataFactory.newBuilder()
                .label(appName + ":" + twoFactorUserDetails.username())
                .secret(secret)
                .issuer(appName)
                .build();


        twoFactorUserDetails.user().setTwoFactorSecret(secret);
        twoFactorUserDetails.user().setTwoFactorSecretExpiryTime(Instant.now());
        userRepository.save(twoFactorUserDetails.user());

        try {
            byte[] qrCode = qrGenerator.generate(data);
            String qrCodeBase64 = Base64.getEncoder().encodeToString(qrCode);
            model.addAttribute("qrCodeBase64", qrCodeBase64);
            model.addAttribute("twoFactorTotpRoute", Helpers.TWO_FACTOR_AUTHENTICATION_TOTP_URL);
        } catch (QrGenerationException e) {
            return "redirect:" + Helpers.LOGOUT_URL;
        }
        return "auth/2fa-qr";
    }

    @PostMapping(Helpers.TWO_FACTOR_AUTHENTICATION_TOTP_URL)
    public String verify2FA(@RequestParam String code) {
        //get user record
        TwoFactorUserDetails twoFactorUserDetails = getTwoFactorUserDetails();
        if (twoFactorUserDetails == null) return "redirect:" + Helpers.LOGOUT_URL;

        //check if the code has been used
        if (twoFactorUserDetails.user.getUsedTotpCodes().contains(code)) {
            return "redirect:" + Helpers.TWO_FACTOR_AUTHENTICATION_TOTP_URL + "?error=code-used";
        }

        //check if the secret has expired
        Instant now = Instant.now();
        Instant secretCreationTime = twoFactorUserDetails.user.getTwoFactorSecretExpiryTime();
        if (secretCreationTime.plus(Duration.ofMinutes(5)).isBefore(now)) {
            return "redirect:" + Helpers.TWO_FACTOR_AUTHENTICATION_TOTP_URL + "?error=expired";
        }

        String secret  = twoFactorUserDetails.user.getTwoFactorSecret();
        //check if code is valid
        if (!codeVerifier.isValidCode(secret, code))
            return "redirect:" + Helpers.TWO_FACTOR_AUTHENTICATION_TOTP_URL + "?error=invalid-code";

        //2FA code is correct,authenticate the user
        session.setAttribute(Helpers.TWO_FACTOR_BOOL_FLAG, true);

        //code is valid
        twoFactorUserDetails.user.getUsedTotpCodes().add(code);
        userRepository.save(twoFactorUserDetails.user);
        return "redirect:" + Helpers.AUTHENTICATED_ROOT_URL;
    }

    private TwoFactorUserDetails getTwoFactorUserDetails() {
        String username = null;
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails userDetails) username = userDetails.getUsername();

        User user = userRepository.findByIdNumber(username);
        if (user == null) {
            return null;
        }
        return new TwoFactorUserDetails(username, user);
    }

    private record TwoFactorUserDetails(String username, User user) {
    }

}
