package com.udacity.jwdnd.course1.cloudstorage.controller;

import com.udacity.jwdnd.course1.cloudstorage.model.User;
import com.udacity.jwdnd.course1.cloudstorage.services.UserService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/signup")
public class SignUpController {
    private UserService userService;

    public SignUpController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String viewPage() {
        return "signup";
    }

    @PostMapping
    public String registerUser(User user, Model model, RedirectAttributes attributes) {
        String signupError = null;

        if (!userService.isUsernameAvailable(user.getUsername())) {
            signupError = "User with name " + user.getUsername() + " already exists";
        }

        if (signupError == null) {
            int userId = userService.createUser(user);
            if (userId < 0) {
                signupError = "User creation failed";
            }
        }

        if (signupError == null) {
            attributes.addFlashAttribute("signupSuccess", true);
            return "redirect:/login";
        } else {
            model.addAttribute("signupError", signupError);
            return "signup";
        }
    }
}
