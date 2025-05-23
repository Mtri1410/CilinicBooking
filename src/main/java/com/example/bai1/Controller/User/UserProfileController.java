package com.example.bai1.Controller.User;

import com.example.bai1.Model.User;
import com.example.bai1.Model.Appointments;
import com.example.bai1.Service.Doctor.AppointmentsService;
import com.example.bai1.Repository.Doctor.UserRepository;
import com.example.bai1.dto.UserUpdateDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.validation.Valid;

import java.util.List;

@Controller
@RequestMapping("/user")
public class UserProfileController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AppointmentsService appointmentsService;

    @GetMapping("/profile")
    public String getUserProfile(Model model) {
        // Lấy thông tin user hiện tại
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userIdentifier = auth.getName(); // Có thể là username hoặc email
        
        // Thử tìm user bằng email
        User user = userRepository.findByAccountEmail(userIdentifier);
        
        // Nếu không tìm thấy bằng email, thử tìm bằng username
        if (user == null) {
            user = userRepository.findByAccountUsername(userIdentifier);
        }

        if (user == null) {
            return "redirect:/SignIn";
        }

        // Lấy danh sách lịch hẹn của user
        List<Appointments> appointments = appointmentsService.getAppointmentsByUser(user);
        
        // Thêm DTO cho form cập nhật thông tin
        if (!model.containsAttribute("userUpdateDTO")) {
            UserUpdateDTO userUpdateDTO = new UserUpdateDTO();
            userUpdateDTO.setFullName(user.getFullName());
            userUpdateDTO.setPhoneNumber(user.getPhoneNumber());
            userUpdateDTO.setAddress(user.getAddress());
            userUpdateDTO.setEmail(user.getAccount().getEmail());
            model.addAttribute("userUpdateDTO", userUpdateDTO);
        }

        model.addAttribute("user", user);
        model.addAttribute("appointments", appointments);
        
        return "user/profile";
    }

    @PostMapping("/profile/update")
    public String updateProfile(@Valid @ModelAttribute UserUpdateDTO userUpdateDTO,
                              BindingResult result,
                              RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.userUpdateDTO", result);
            redirectAttributes.addFlashAttribute("userUpdateDTO", userUpdateDTO);
            return "redirect:/user/profile";
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userIdentifier = auth.getName();
        
        // Try to find user by email first
        User user = userRepository.findByAccountEmail(userIdentifier);
        
        // If not found by email, try by username
        if (user == null) {
            user = userRepository.findByAccountUsername(userIdentifier);
        }

        if (user != null) {
            // Check if email is already taken by another user
            User existingUserWithEmail = userRepository.findByAccountEmail(userUpdateDTO.getEmail());
            if (existingUserWithEmail != null && !existingUserWithEmail.getUserId().equals(user.getUserId())) {
                redirectAttributes.addFlashAttribute("errorMessage", "Email đã được sử dụng bởi tài khoản khác!");
                redirectAttributes.addFlashAttribute("userUpdateDTO", userUpdateDTO);
                return "redirect:/user/profile";
            }

            user.setFullName(userUpdateDTO.getFullName());
            user.setPhoneNumber(userUpdateDTO.getPhoneNumber());
            user.setAddress(userUpdateDTO.getAddress());
            user.getAccount().setEmail(userUpdateDTO.getEmail());
            userRepository.save(user);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật thông tin thành công!");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy thông tin người dùng!");
        }

        return "redirect:/user/profile";
    }

    @PostMapping("/appointments/{id}/cancel")
    public String cancelAppointment(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            System.out.println("Bắt đầu xử lý hủy lịch hẹn với ID: " + id);
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String userIdentifier = auth.getName();
            
            // Thử tìm user bằng email
            User user = userRepository.findByAccountEmail(userIdentifier);
            
            // Nếu không tìm thấy bằng email, thử tìm bằng username
            if (user == null) {
                user = userRepository.findByAccountUsername(userIdentifier);
            }

            if (user == null) {
                System.out.println("Không tìm thấy thông tin người dùng với identifier: " + userIdentifier);
                redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy thông tin người dùng!");
                return "redirect:/user/profile";
            }
            
            // Kiểm tra xem lịch hẹn có tồn tại và thuộc về user không
            Appointments appointment = appointmentsService.getappointments(id);
            if (appointment == null) {
                System.out.println("Không tìm thấy lịch hẹn với ID: " + id);
                redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy lịch hẹn!");
                return "redirect:/user/profile";
            }
            
            if (!appointment.getPatient().getUserId().equals(user.getUserId())) {
                System.out.println("User ID " + user.getUserId() + " không có quyền hủy lịch hẹn ID " + id + 
                                 " của patient ID " + appointment.getPatient().getUserId());
                redirectAttributes.addFlashAttribute("errorMessage", "Bạn không có quyền hủy lịch hẹn này!");
                return "redirect:/user/profile";
            }
            
            System.out.println("Đang hủy lịch hẹn với ID: " + id + ", trạng thái hiện tại: " + appointment.getStatus());
            boolean success = appointmentsService.cancelAppointment(id, user);
            
            if (success) {
                System.out.println("Hủy lịch hẹn thành công với ID: " + id);
                redirectAttributes.addFlashAttribute("successMessage", "Hủy lịch hẹn thành công!");
            } else {
                System.out.println("Hủy lịch hẹn thất bại với ID: " + id);
                redirectAttributes.addFlashAttribute("errorMessage", "Không thể hủy lịch hẹn. Vui lòng thử lại sau!");
            }
            
            return "redirect:/user/profile";
        } catch (Exception e) {
            System.out.println("Lỗi xử lý hủy lịch hẹn: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi hệ thống: " + e.getMessage());
            return "redirect:/user/profile";
        }
    }

    @PostMapping("/appointments/{id}/reschedule")
    public String rescheduleAppointment(@PathVariable Long id,
                                      @RequestParam String newDate,
                                      @RequestParam String newTime,
                                      RedirectAttributes redirectAttributes) {
        try {
            // Lấy thông tin người dùng hiện tại
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Bạn cần đăng nhập để thực hiện chức năng này!");
                return "redirect:/SignIn";
            }
            
            // Lấy user từ email đăng nhập
            User user = userRepository.findByAccountEmail(auth.getName());
            if (user == null) {
                // Thử lấy user từ tên tài khoản nếu không tìm thấy bằng email
                user = userRepository.findByAccountUsername(auth.getName());
            }
            
            if (user == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy thông tin người dùng!");
                return "redirect:/user/profile";
            }
            
            boolean success = appointmentsService.rescheduleAppointment(id, user, newDate, newTime);
            
            if (success) {
                redirectAttributes.addFlashAttribute("successMessage", "Đổi lịch hẹn thành công!");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Không thể đổi lịch hẹn. Vui lòng thử lại sau!");
            }
            
            return "redirect:/user/profile";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi hệ thống: " + e.getMessage());
            return "redirect:/user/profile";
        }
    }
} 