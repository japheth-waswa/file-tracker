package com.elijahwaswa.filetracker.controller;

import com.elijahwaswa.filetracker.config.CustomUserDetails;
import com.elijahwaswa.filetracker.util.Helpers;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class TwoFactorController {
    private final HttpSession session;

    public TwoFactorController(HttpSession session) {
        this.session = session;
    }

    @GetMapping(Helpers.TWO_FACTOR_AUTHENTICATION_URL)
    public String show2FA(Model model) {
        model.addAttribute("twoFactorRoute",Helpers.TWO_FACTOR_AUTHENTICATION_URL);
        return "auth/2fa";
    }

    @PostMapping(Helpers.TWO_FACTOR_AUTHENTICATION_URL)
    public String verify2FA(@RequestParam String twoFactorCode) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof CustomUserDetails userDetails) {
            if (userDetails.getTwoFactorCode().equalsIgnoreCase(twoFactorCode)) {
                //2FA code is correct,authenticate the user
                session.setAttribute(Helpers.TWO_FACTOR_BOOL_FLAG, true);
                return "redirect:" + Helpers.AUTHENTICATED_ROOT_URL;
            }
        }
        // 2FA code is incorrect, redirect back to 2fa page
        session.setAttribute(Helpers.TWO_FACTOR_BOOL_FLAG, false);
        return "redirect:" + Helpers.TWO_FACTOR_AUTHENTICATION_URL;
    }

}
