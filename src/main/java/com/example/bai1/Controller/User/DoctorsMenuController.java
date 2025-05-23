package com.example.bai1.Controller.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.bai1.Service.Doctor.DoctorService;
import com.example.bai1.Model.Doctor;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/doctors-menu")
public class DoctorsMenuController {

    @Autowired
    private DoctorService doctorService;

    @GetMapping
    public String showDoctorsList(
            @RequestParam(required = false) String specialty,
            @RequestParam(required = false) String search,
            Model model) {
        
        List<Doctor> doctors;
        if (specialty != null && !specialty.isEmpty()) {
            doctors = doctorService.findBySpecialty(specialty);
        } else if (search != null && !search.isEmpty()) {
            doctors = doctorService.searchDoctors(search);
        } else {
            doctors = doctorService.getAllDoctors();
        }

        // Lấy danh sách các chuyên khoa duy nhất từ tất cả bác sĩ
        Set<String> specialties = doctorService.getAllDoctors().stream()
            .map(Doctor::getSpecialty)
            .filter(spec -> spec != null && !spec.isEmpty())
            .collect(Collectors.toSet());

        model.addAttribute("doctors", doctors);
        model.addAttribute("specialties", specialties);
        model.addAttribute("pageTitle", "Danh sách bác sĩ");
        model.addAttribute("currentSpecialty", specialty);
        return "user/doctors-menu";
    }
} 