package com.example.bai1.Controller.Doctor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.bai1.Model.Account;
import com.example.bai1.Model.CustomUserDetail;
import com.example.bai1.Model.Doctor;
import com.example.bai1.Model.Products;
import com.example.bai1.Service.Doctor.DoctorService;
import com.example.bai1.Service.Doctor.ProductsService;

@Controller
public class DoctorController {

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private ProductsService productsService;
    @Autowired
    private DoctorService doctorService;

    DoctorController(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/doctor/dashboard")
    public String Dashboard(Model model) {
        model.addAttribute("message", "Hello, Spring Boot!");
        return "Doctor/Index";
    }

    @GetMapping("/doctor/product")
    public String product(Model model) {
        long id;
        Products product = productsService.getProductsidDesc();
        if (product == null) {
            id = 1;
        } else {
            id = product.getProductId() + 1;
        }
        Products newproduct = new Products();
        newproduct.setProductId(id);
        model.addAttribute("product", newproduct);

        return "Doctor/Product";
    }

    @GetMapping("/doctor/order")
    public String Order(Model model) {
        model.addAttribute("message", "Hello, Spring Boot!");
        return "Doctor/Order";
    }

    @GetMapping("/doctor/appointment")
    public String Appointment(Model model) {
        model.addAttribute("message", "Hello, Spring Boot!");
        return "Doctor/Appointment";
    }

    @GetMapping("/doctor/information")
    public String Information(Model model) {
        model.addAttribute("message", "Hello, Spring Boot!");
        return "Doctor/Index";
    }

    @GetMapping("/doctor/doctorinfo")
    public String DoctorInfo(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetail userDetail = (CustomUserDetail) auth.getPrincipal();
        Account account = userDetail.getAccount();
        Doctor doctor = doctorService.getinfo(account);
        System.out.println("Thoi gian la --------------" + doctor.getBirthday());
        model.addAttribute("doctor", doctor);
        return "Doctor/DoctorInfo";
    }

    @GetMapping("/doctor/upmembership")
    public String UpMembership(Model model) {
        model.addAttribute("message", "Hello, Spring Boot!");
        return "Doctor/Membershipdetail";
    }

    @GetMapping("/doctor/comfirmappointment")
    public String Comfirmappointment(Model model) {
        model.addAttribute("message", "Hello, Spring Boot!");
        return "Doctor/ComfirmAppointment";
    }

    @PostMapping("/doctor/profile/save")
    public String SaveDoctor(@ModelAttribute Doctor doctor, @RequestParam(required = false) String currentPassword,
            @RequestParam(required = false) String newPassword, RedirectAttributes redirectAttributes,
            @RequestParam(required = false, defaultValue = "false") boolean changepass) {
        Doctor existingDoctor = doctorService.findbydoctorid(doctor.getDoctorId());
        System.out.println("-------------------------------------" + doctor.getFullname());
        if (existingDoctor == null) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy bác sĩ.");
            return "redirect:/doctor/doctorinfo";
        }
        existingDoctor.setFullname(doctor.getFullname());
        existingDoctor.setSpecialty(doctor.getSpecialty());
        existingDoctor.setLicense(doctor.getLicense());
        existingDoctor.setAddress(doctor.getAddress());
        existingDoctor.setGender(doctor.getGender());
        existingDoctor.setBirthday(doctor.getBirthday());
        existingDoctor.getAccount().setEmail(doctor.getAccount().getEmail());
        System.out.println("-------------------------------" + currentPassword);
        if (changepass) {
            if (!passwordEncoder.matches(currentPassword, existingDoctor.getAccount().getPassword())) {
                redirectAttributes.addFlashAttribute("error", "Mật khẩu hiện tại không đúng.");
                return "redirect:/doctor/doctorinfo";
            }
            String encodedPassword = passwordEncoder.encode(newPassword);
            existingDoctor.getAccount().setPassword(encodedPassword);
        }
        doctorService.save(existingDoctor);
        redirectAttributes.addFlashAttribute("success", "Cập nhật thông tin thành công.");
        return "redirect:/doctor/doctorinfo";
    }

}
