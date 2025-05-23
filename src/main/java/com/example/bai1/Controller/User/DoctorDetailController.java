package com.example.bai1.Controller.User;

import com.example.bai1.Model.Doctor;
import com.example.bai1.Service.Doctor.DoctorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/doctors")
public class DoctorDetailController {

    @Autowired
    private DoctorService doctorService;

    @GetMapping("/{id}")
    public String getDoctorDetail(@PathVariable("id") Long id, Model model) {
        Doctor doctor = doctorService.getDoctorById(id);
        if (doctor == null) {
            return "redirect:/error";
        }
        model.addAttribute("doctor", doctor);
        return "user/doctor-detail";
    }
} 