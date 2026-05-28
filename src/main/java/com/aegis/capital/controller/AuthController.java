package com.aegis.capital.controller;

import com.aegis.capital.config.AccountsConstants;
import com.aegis.capital.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String login(@RequestParam(required = false) String error,
                        @RequestParam(required = false) String logout,
                        Model model) {
        var hasError = error != null;
        var hasLogout = logout != null;

        if (hasError) model.addAttribute(AccountsConstants.MODEL_ERROR, AccountsConstants.LOGIN_ERROR);
        if (hasLogout) model.addAttribute("message", AccountsConstants.LOGOUT_MSG);

        return AccountsConstants.VIEW_LOGIN;
    }

    @GetMapping("/register")
    public String showRegistrationForm() {
        return AccountsConstants.VIEW_REGISTER;
    }

    @PostMapping("/register")
    public String registerUser(@RequestParam String username,
                               @RequestParam String password,
                               @RequestParam String confirmPassword,
                               @RequestParam String email,
                               @RequestParam String fullName,
                               @RequestParam String phone,
                               Model model) {
        if (!password.equals(confirmPassword)) {
            model.addAttribute(AccountsConstants.MODEL_ERROR, AccountsConstants.PASSWORDS_MISMATCH);
            return AccountsConstants.VIEW_REGISTER;
        }

        try {
            userService.registerUser(username, password, email, fullName, phone);
            log.info("New user registered: {}", username);
            return AccountsConstants.VIEW_REGISTER_REDIRECT;
        } catch (Exception e) {
            log.error("Registration failed for '{}': {}", username, e.getMessage());
            model.addAttribute(AccountsConstants.MODEL_ERROR, e.getMessage());
            return AccountsConstants.VIEW_REGISTER;
        }
    }

    @GetMapping("/dashboard")
    public String dashboard(Authentication auth) {
        var isAdmin = auth.getAuthorities().stream()
                .anyMatch(g -> g.getAuthority().equals(AccountsConstants.ROLE_ADMIN));
        return isAdmin
                ? "redirect:/admin/dashboard"
                : "redirect:/user/dashboard";
    }
}
