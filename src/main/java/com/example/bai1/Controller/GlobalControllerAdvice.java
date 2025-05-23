package com.example.bai1.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.security.core.Authentication;
import com.example.bai1.Model.Account;
import com.example.bai1.Model.CustomUserDetail;
import com.example.bai1.Model.Doctor;
import com.example.bai1.Repository.Doctor.DoctorRepository;

@Component
@ControllerAdvice
public class GlobalControllerAdvice {
    @Autowired
    private DoctorRepository doctorRepository;

    @ModelAttribute("Doctor")
    public Doctor getLoggedInDoctor() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof CustomUserDetail) {
            CustomUserDetail userDetail = (CustomUserDetail) auth.getPrincipal();
            Account account = userDetail.getAccount();
            return doctorRepository.findByAccount(account);
        }
        return null;
    }
}
