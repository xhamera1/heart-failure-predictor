package com.xhamera1.heart_failure_predictor.controller;

import com.xhamera1.heart_failure_predictor.dto.UserRegistrationDto;
import com.xhamera1.heart_failure_predictor.exceptions.ResourceAlreadyExistsException;
import com.xhamera1.heart_failure_predictor.model.User;
import com.xhamera1.heart_failure_predictor.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/auth")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);
    private final UserService userService;

    @Autowired
    public AuthController(UserService userService) {
        this.userService = userService;
    }


    @GetMapping("/register")
    public String getRegisterForm(Model model) {
        if (!model.containsAttribute("userRegistrationInfo")) {
            model.addAttribute("userRegistrationInfo", new UserRegistrationDto());
        }
        return "register";
    }

    @PostMapping("/register")
    public String processRegistration(
            @Valid @ModelAttribute("userRegistrationInfo") UserRegistrationDto registrationDto,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {
        log.info("Processing registration attempt for username: {}", registrationDto.getUsername());
        if (bindingResult.hasErrors()) {
            log.warn("Registration form validation failed for username {}: {}", registrationDto.getUsername(), bindingResult.getAllErrors());
            return "register";
        }

        try {
            userService.addUser(registrationDto);
            log.info("User registered successfully: {}", registrationDto.getUsername());
            redirectAttributes.addFlashAttribute("registrationSuccess", "Registration success! You can log in now.");
            return "redirect:/auth/login";
        } catch (ResourceAlreadyExistsException e) {
            log.warn("Registration failed for username {}: {}", registrationDto.getUsername(), e.getMessage());
            bindingResult.reject("registrationError", e.getMessage());
            return "register";
        } catch (Exception e) {
            log.error("Unexpected error during registration for username: {}", registrationDto.getUsername(), e);
            bindingResult.reject("registrationError", "Unexpected error in registration process");
            return "register";
        }
    }

    @GetMapping("/login")
    public String showLoginForm() {
        log.debug("Displaying login form.");
        return "login";
    }

}
