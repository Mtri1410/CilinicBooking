package com.example.bai1.Controller.User;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/user")
public class UserHomeController {

    @GetMapping("/home")
    public String userHomePage(Model model, Authentication authentication, HttpServletRequest request) {
        if (authentication != null && authentication.isAuthenticated()) {
            model.addAttribute("username", authentication.getName());
        }
        model.addAttribute("pageTitle", "Trang chủ Người dùng");
        // For potential layout active link, though this page won't have a shared layout
        model.addAttribute("currentUri", request.getRequestURI());
        return "user/home";
    }
}