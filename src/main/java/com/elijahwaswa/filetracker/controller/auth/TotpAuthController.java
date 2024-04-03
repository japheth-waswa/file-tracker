package com.elijahwaswa.filetracker.controller.auth;

import com.elijahwaswa.filetracker.model.User;
import com.elijahwaswa.filetracker.repository.UserRepository;
import com.elijahwaswa.filetracker.service.user.UserService;
import com.elijahwaswa.filetracker.util.Helpers;
import com.elijahwaswa.filetracker.util.TwoFactorPayload;
import dev.samstevens.totp.code.CodeVerifier;
import dev.samstevens.totp.exceptions.QrGenerationException;
import dev.samstevens.totp.qr.QrData;
import dev.samstevens.totp.qr.QrDataFactory;
import dev.samstevens.totp.qr.QrGenerator;
import dev.samstevens.totp.secret.SecretGenerator;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.Duration;
import java.time.Instant;
import java.util.Base64;

@Controller
public class TotpAuthController {

    private final HttpSession session;
    private final SecretGenerator secretGenerator;
    private final QrDataFactory qrDataFactory;
    private final QrGenerator qrGenerator;
    private final CodeVerifier codeVerifier;
    private final UserRepository userRepository;
    private final UserService userService;
    private final Logger LOGGER = LoggerFactory.getLogger(TotpAuthController.class);

    @Value("${spring.application.name}")
    private String appName;

    public TotpAuthController(HttpSession session, SecretGenerator secretGenerator, QrDataFactory qrDataFactory, QrGenerator qrGenerator, CodeVerifier codeVerifier, UserRepository userRepository, UserService userService) {
        this.session = session;
        this.secretGenerator = secretGenerator;
        this.qrDataFactory = qrDataFactory;
        this.qrGenerator = qrGenerator;
        this.codeVerifier = codeVerifier;
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @GetMapping(Helpers.TWO_FACTOR_AUTHENTICATION_TOTP_URL)
    public String showSetup2FA(Model model) {
        TwoFactorPayload twoFactorPayload;
        try {
            String username = Helpers.getLoggedInUsername();
            twoFactorPayload = userService.generateToTpQRCodeBase64Encoded(username, appName);
            if (!twoFactorPayload.dbSecretExists()) {
                //show QrCode if secret does not exist
                model.addAttribute("qrCodeBase64", twoFactorPayload.qrCodeBase64());
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return "redirect:" + Helpers.LOGOUT_URL;
        }

        model.addAttribute("twoFactorTotpRoute", Helpers.TWO_FACTOR_AUTHENTICATION_TOTP_URL);
        model.addAttribute("topNavAllowed", false);
        model.addAttribute("sideNavAllowed", false);
        return "auth/2fa-qr";
    }

    @PostMapping(Helpers.TWO_FACTOR_AUTHENTICATION_TOTP_URL)
    public String verify2FA(@RequestParam String code) {
        //get user record
        TwoFactorPayload twoFactorPayload = getTwoFactorUserDetails();
        if (twoFactorPayload == null) return "redirect:" + Helpers.LOGOUT_URL;

        //check if the code has been used
        if (twoFactorPayload.user().getUsedTotpCodes().contains(code)) {
            return "redirect:" + Helpers.TWO_FACTOR_AUTHENTICATION_TOTP_URL + "?error=code-used";
        }

        //check if the secret has expired
        Instant now = Instant.now();
        Instant secretCreationTime = twoFactorPayload.user().getTwoFactorSecretExpiryTime();
        if (secretCreationTime.plus(Duration.ofMinutes(Helpers.OTP_EXPIRY_MINUTES)).isBefore(now)) {
            return "redirect:" + Helpers.TWO_FACTOR_AUTHENTICATION_TOTP_URL + "?error=expired";
        }

        String secret = twoFactorPayload.user().getTwoFactorSecret();
        //check if code is valid
        if (!codeVerifier.isValidCode(secret, code))
            return "redirect:" + Helpers.TWO_FACTOR_AUTHENTICATION_TOTP_URL + "?error=invalid-code";

        //2FA code is correct,authenticate the user
        session.setAttribute(Helpers.TWO_FACTOR_BOOL_FLAG, true);

        //code is valid
        twoFactorPayload.user().getUsedTotpCodes().add(code);
        userRepository.save(twoFactorPayload.user());
        return "redirect:" + Helpers.AUTHENTICATED_ROOT_URL;
    }

    private TwoFactorPayload getTwoFactorUserDetails() {
        String username = Helpers.getLoggedInUsername();
        User user = userRepository.findByIdNumber(username);
        if (user == null) {
            return null;
        }
        return new TwoFactorPayload(username, user, null,false);
    }
}
