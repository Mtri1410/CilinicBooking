package com.example.bai1.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.bai1.Model.Account;
import com.example.bai1.Service.Doctor.AccountService;

@Controller
public class MyController {

    @Autowired
    private AccountService accountService;

    @GetMapping("/home")
    public String hello(Model model) {
        model.addAttribute("message", "Hello, Spring Boot!");
        return "hello";
    }

    @GetMapping("/")
    public String userhome() {
        return "redirect:/user/home";
    }

    @GetMapping("/doctor")
    public String doctor(Model model) {
        return "Doctor/Index";
    }

    @GetMapping("/SignIn")
    public String Signin(Model model) {
        return "SignIn";
    }

    @GetMapping("/SignUp")
    public String Signup(Model model) {
        model.addAttribute("account", new Account());
        return "SignUp";
    }

    @PostMapping("/SignUp")
    public String SigupUser(@ModelAttribute("account") Account account, @RequestParam String confirmpass,
            @RequestParam("adress") String address, @RequestParam("phonenumber") String phone,
            @RequestParam("fullname") String fullname) {
        if (!account.getPassword().equals(confirmpass)) {
            return "redirect:/SignUp?error=Passwords do not match";
        }
        boolean success = accountService.registerAccount(account, phone, address, fullname);
        if (!success) {
            return "redirect:/SignUp?error=Email or Username already exists";
        }
        return "redirect:/SignIn?success=Sigin is success";
    }

    @GetMapping("/EmailOTP")
    public String OTP(Model model) {
        model.addAttribute("account", new Account());
        return "EmailOTP";
    }

    @GetMapping("/reset-password")
    public String changepass(Model model) {
        model.addAttribute("account", new Account());
        return "ChangeNewPass";
    }
}
