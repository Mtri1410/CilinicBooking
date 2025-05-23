package com.example.bai1.Controller.User;

import com.example.bai1.Model.Reviews;
import com.example.bai1.Model.Doctor;
import com.example.bai1.Model.User;
import com.example.bai1.Model.Account;
import com.example.bai1.Repository.Doctor.DoctorRepository;
import com.example.bai1.Repository.Doctor.UserRepository;
import com.example.bai1.Service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/doctor/{doctorId}")
public class ReviewRestController {
    @Autowired
    private ReviewService reviewService;
    @Autowired
    private DoctorRepository doctorRepository;
    @Autowired
    private UserRepository userRepository;

    // Lấy danh sách review đã duyệt cho 1 bác sĩ
    @GetMapping("/reviews")
    public List<ReviewDTO> getApprovedReviews(@PathVariable Long doctorId) {
        List<Reviews> reviews = reviewService.findAllReviews().stream()
                .filter(r -> r.getDoctor() != null && r.getDoctor().getDoctorId().equals(doctorId))
                .filter(r -> r.getStatus() == Reviews.ReviewStatus.APPROVED)
                .collect(Collectors.toList());
        return reviews.stream().map(ReviewDTO::fromEntity).collect(Collectors.toList());
    }

    // Gửi review mới (chỉ cho bệnh nhân)
    @PostMapping("/review")
    public ResponseEntity<?> postReview(@PathVariable Long doctorId, @RequestBody ReviewRequest req) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return ResponseEntity.status(401).body("Bạn cần đăng nhập để đánh giá.");
        }
        String username = auth.getName();
        User user = userRepository.findByAccountUsername(username);
        if (user == null) {
            user = userRepository.findByAccountEmail(username);
        }
        if (user == null) {
            return ResponseEntity.status(403).body("Không tìm thấy thông tin người dùng.");
        }
        Doctor doctor = doctorRepository.findByDoctorId(doctorId);
        if (doctor == null) {
            return ResponseEntity.badRequest().body("Bác sĩ không tồn tại.");
        }
        Reviews review = Reviews.builder()
                .doctor(doctor)
                .patient(user)
                .rating(req.rating)
                .comment(req.comment)
                .status(Reviews.ReviewStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();
        reviewService.saveReview(review);
        return ResponseEntity.ok("Đã gửi đánh giá, chờ duyệt!");
    }

    // DTO cho response
    public static class ReviewDTO {
        public Long id;
        public String username;
        public int rating;
        public String comment;
        public String createdAt;
        public static ReviewDTO fromEntity(Reviews r) {
            ReviewDTO dto = new ReviewDTO();
            dto.id = r.getReviewId();
            dto.username = r.getPatient() != null ? r.getPatient().getFullName() : "Ẩn danh";
            dto.rating = r.getRating();
            dto.comment = r.getComment();
            dto.createdAt = r.getCreatedAt() != null ? r.getCreatedAt().toString() : "";
            return dto;
        }
    }
    // DTO cho request
    public static class ReviewRequest {
        public int rating;
        public String comment;
    }
} 