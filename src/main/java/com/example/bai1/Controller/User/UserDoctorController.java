package com.example.bai1.Controller.User;

import com.example.bai1.Model.Account;
import com.example.bai1.Model.Doctor;
import com.example.bai1.Service.Doctor.DoctorService;
import com.example.bai1.Repository.Doctor.AccountRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.validation.Valid;

import java.time.LocalDateTime;

@Controller
@RequestMapping("/user")
public class UserDoctorController {

    private static final Logger logger = LoggerFactory.getLogger(UserDoctorController.class);

    private final DoctorService doctorService;
    private final AccountRepository accountRepository;

    @Autowired
    public UserDoctorController(DoctorService doctorService, AccountRepository accountRepository) {
        this.doctorService = doctorService;
        this.accountRepository = accountRepository;
    }

    @GetMapping("/register-doctor")
    public String showDoctorRegistrationForm(Model model, Authentication authenticationFromParam, RedirectAttributes redirectAttributes) {
        Authentication authFromContext = SecurityContextHolder.getContext().getAuthentication();

        logger.info("UserDoctorController - showDoctorRegistrationForm hit.");
        logger.info("Authentication from Parameter: Name='{}', IsAuthenticated='{}', Authorities='{}'", 
            (authenticationFromParam == null ? "null" : authenticationFromParam.getName()), 
            (authenticationFromParam == null ? "null" : authenticationFromParam.isAuthenticated()),
            (authenticationFromParam == null ? "null" : authenticationFromParam.getAuthorities()));
        logger.info("Authentication from SecurityContextHolder: Name='{}', IsAuthenticated='{}', Authorities='{}'", 
            (authFromContext == null ? "null" : authFromContext.getName()), 
            (authFromContext == null ? "null" : authFromContext.isAuthenticated()),
            (authFromContext == null ? "null" : authFromContext.getAuthorities()));

        if (authenticationFromParam == null || !authenticationFromParam.isAuthenticated()) {
            logger.warn("Redirecting to /SignIn: authenticationFromParam is null or not authenticated.");
            return "redirect:/SignIn"; 
        }
        
        String currentPrincipalName = authenticationFromParam.getName();
        logger.info("Attempting to find account by username (principal name): {}", currentPrincipalName);
        Account currentUserAccount = accountRepository.findByUsername(currentPrincipalName);

        if (currentUserAccount == null) {
            if (currentPrincipalName.contains("@")) {
                logger.info("Account not found by username, trying by email: {}", currentPrincipalName);
                currentUserAccount = accountRepository.findByEmail(currentPrincipalName);
            }
        }

        if (currentUserAccount == null) {
            logger.warn("Redirecting to /SignIn: currentUserAccount is null for principal '{}' (tried username and potentially email).", currentPrincipalName);
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy tài khoản người dùng ('" + currentPrincipalName + "'). Vui lòng thử đăng nhập lại.");
            return "redirect:/SignIn"; 
        }

        Doctor existingDoctor = doctorService.getinfo(currentUserAccount);
        if (existingDoctor != null) {
            logger.info("User '{}' already has a doctor record with status: {}", currentPrincipalName, existingDoctor.getStatus());
            if (existingDoctor.getStatus() == Doctor.Status.APPROVED) {
                redirectAttributes.addFlashAttribute("infoMessage", "Tài khoản của bạn đã được duyệt làm bác sĩ.");
                return "redirect:/doctor/dashboard"; 
            } else if (existingDoctor.getStatus() == Doctor.Status.PENDING) {
                redirectAttributes.addFlashAttribute("infoMessage", "Đơn đăng ký bác sĩ của bạn đang chờ duyệt.");
                return "redirect:/user/home"; 
            } else if (existingDoctor.getStatus() == Doctor.Status.REJECTED) {
                redirectAttributes.addFlashAttribute("errorMessage", "Đơn đăng ký bác sĩ của bạn đã bị từ chối. Vui lòng liên hệ quản trị viên.");
                return "redirect:/user/home"; 
            }
        }
        logger.info("User '{}' does not have an existing doctor record. Displaying registration form.", currentPrincipalName);
        model.addAttribute("doctor", new Doctor());
        model.addAttribute("pageTitle", "Đăng ký trở thành Bác sĩ");
        model.addAttribute("currentUri", "/user/register-doctor"); 
        return "user/register-doctor";
    }

    @PostMapping("/register-doctor")
    public String processDoctorRegistration(@Valid @ModelAttribute("doctor") Doctor doctor,
                                            BindingResult result,
                                            Authentication authentication,
                                            RedirectAttributes redirectAttributes,
                                            Model model) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/SignIn";
        }

        String currentPrincipalNameFromPost = authentication.getName();
        logger.info("POST /register-doctor - Attempting to find account by username (principal name): {}", currentPrincipalNameFromPost);
        Account currentUserAccountFromPost = accountRepository.findByUsername(currentPrincipalNameFromPost);

        if (currentUserAccountFromPost == null) {
            if (currentPrincipalNameFromPost.contains("@")) {
                 logger.info("POST /register-doctor - Account not found by username, trying by email: {}", currentPrincipalNameFromPost);
                currentUserAccountFromPost = accountRepository.findByEmail(currentPrincipalNameFromPost);
            }
        }

        if (currentUserAccountFromPost == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy tài khoản người dùng. Không thể gửi đơn đăng ký.");
            return "redirect:/user/register-doctor";
        }
        
        Doctor existingDoctor = doctorService.getinfo(currentUserAccountFromPost);
        if (existingDoctor != null) {
           redirectAttributes.addFlashAttribute("errorMessage", "Tài khoản này đã có đơn đăng ký bác sĩ (" + existingDoctor.getStatus() + ").");
           return "redirect:/user/register-doctor"; 
        }

        if (result.hasErrors()) {
            model.addAttribute("pageTitle", "Đăng ký trở thành Bác sĩ - Lỗi");
            model.addAttribute("currentUri", "/user/register-doctor");
            return "user/register-doctor";
        }

        try {
            doctor.setAccount(currentUserAccountFromPost);
            doctor.setStatus(Doctor.Status.PENDING); 
            doctor.setCreatedAt(LocalDateTime.now());
            doctor.setUpdatedAt(LocalDateTime.now());

            doctorService.save(doctor);
            redirectAttributes.addFlashAttribute("successMessage", "Yêu cầu đăng ký bác sĩ của bạn đã được gửi. Vui lòng chờ duyệt.");
            return "redirect:/"; 
        } catch (Exception e) {
            logger.error("Error during doctor registration for user '{}': {}", currentPrincipalNameFromPost, e.getMessage(), e);
            redirectAttributes.addFlashAttribute("errorMessage", "Đã xảy ra lỗi trong quá trình đăng ký: " + e.getMessage());
            model.addAttribute("pageTitle", "Đăng ký trở thành Bác sĩ - Lỗi");
            model.addAttribute("currentUri", "/user/register-doctor");
            return "user/register-doctor";
        }
    }
} 