package com.example.bai1.Controller.Doctor;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.bai1.Model.Account;
import com.example.bai1.Model.CustomUserDetail;
import com.example.bai1.Model.Doctor;
import com.example.bai1.Model.Memberships;
import com.example.bai1.Model.Order_items;
import com.example.bai1.Model.Orders;
import com.example.bai1.Model.Products;
import com.example.bai1.Model.order_membership;
import com.example.bai1.Model.order_membership.OrderType;
import com.example.bai1.Model.order_membership.Status;
import com.example.bai1.Repository.Doctor.DoctorRepository;
import com.example.bai1.Service.OrderMembershipService;
import com.example.bai1.Service.VnPayService;
import com.example.bai1.Service.Doctor.MembershipsService;
import com.example.bai1.Service.Doctor.OrderItemService;
import com.example.bai1.Service.Doctor.OrderService;
import com.example.bai1.Service.Doctor.ProductsService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/membershipdetail")
public class MembershipsController {
    @Autowired
    private final OrderService orderserService;
    @Autowired
    private final OrderItemService orderItemService;
    @Autowired
    private final ProductsService productsService;
    @Autowired
    private final DoctorRepository doctorRepository;
    @Autowired
    private MembershipsService membershipsService;
    @Autowired
    private OrderMembershipService orderMembershipService;
    @Autowired
    private VnPayService vnPayService;

    @GetMapping("/giahan")
    public ResponseEntity<?> getgiahan() {
        // Lấy thông tin người dùng hiện tại
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetail userDetail = (CustomUserDetail) auth.getPrincipal();
        Account account = userDetail.getAccount();
        Doctor doctor = doctorRepository.findByAccount(account);
        Memberships memberships = doctor.getMembership();
        Map<String, Object> response = new HashMap<>();
        response.put("date", doctor.getEnddate()); // đưa tên member vào
        response.put("price", memberships.getPrice());
        return ResponseEntity.ok(response);

    }

    @GetMapping("/nangcap")
    public ResponseEntity<?> getnangcap() {
        // Lấy thông tin người dùng hiện tại
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetail userDetail = (CustomUserDetail) auth.getPrincipal();
        Account account = userDetail.getAccount();
        Doctor doctor = doctorRepository.findByAccount(account);
        Memberships member = membershipsService.findbyid(doctor.getMembership().getMembershipId());
        List<Memberships> ds = membershipsService.getAllMemberships();
        Map<String, Object> response = new HashMap<>();
        response.put("memberName", member.getName()); // đưa tên member vào
        response.put("membershipList", ds);
        return ResponseEntity.ok(response);

    }

    @PostMapping("/xulygiahan")
    public ResponseEntity<?> giaHan(@RequestBody Map<String, Object> payload) {
        try {
            double price = Double.parseDouble(payload.get("price").toString());
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            CustomUserDetail userDetail = (CustomUserDetail) auth.getPrincipal();
            Account account = userDetail.getAccount();
            Doctor doctor = doctorRepository.findByAccount(account);
            if (doctor == null) {
                return ResponseEntity.badRequest().body("Không tìm thấy bác sĩ tương ứng");
            }
            order_membership order_membership = new order_membership();
            order_membership.setMembership(doctor.getMembership());
            order_membership.setDoctor(doctor);
            long duration = (long) (price
                    / membershipsService.findbyid(doctor.getMembership().getMembershipId()).getPrice());
            order_membership.setCount(duration);
            order_membership.setTotalprice(BigDecimal.valueOf(price));
            order_membership.setStatus(Status.APPROVED);
            order_membership.setType(OrderType.RENEWAL);

            doctor.setEnddate(doctor.getEnddate().plusMonths(duration));
            doctorRepository.save(doctor);
            orderMembershipService.save(order_membership);
            return ResponseEntity.ok("Đã gia hạn thêm " + duration + " tháng");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Lỗi server: " + e.getMessage());
        }
    }

    @PostMapping("/xulynangcap")
    public ResponseEntity<?> nangCapMembership(@RequestBody Map<String, Object> payload) {
        // Lấy giá trị membershipId từ payload JSON
        Object membershipIdObj = payload.get("membershipId");
        if (membershipIdObj == null) {
            return ResponseEntity.badRequest().body("membershipId is required");
        }
        Long membershipId;
        try {
            // Nếu payload gửi membershipId dưới dạng số hoặc chuỗi số, ta parse sang Long
            membershipId = Long.parseLong(membershipIdObj.toString());
            Memberships memberships = membershipsService.findbyid(membershipId);
            double price = memberships.getPrice();
            order_membership order_membership = new order_membership();
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            CustomUserDetail userDetail = (CustomUserDetail) auth.getPrincipal();
            Account account = userDetail.getAccount();
            Doctor doctor = doctorRepository.findByAccount(account);
            order_membership.setCount(Long.valueOf(1));
            order_membership.setDoctor(doctor);
            order_membership.setMembership(memberships);
            order_membership.setTotalprice(BigDecimal.valueOf(price));
            order_membership.setStatus(Status.APPROVED);

            order_membership.setType(OrderType.UPGRADE);
            orderMembershipService.save(order_membership);
            doctor.setMembership(memberships);
            doctor.setStartdate(LocalDate.now());
            doctor.setEnddate(LocalDate.now().plusMonths(1));
            doctorRepository.save(doctor);
            // Tạm thời chưa xử lý gì, chỉ trả về message
            String responseMessage = "Đã nhận membershipId: " + membershipId;
            return ResponseEntity.ok(responseMessage);
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body("membershipId must be a number");
        }

    }

    @PostMapping("/xulygiahanvnpay")
    public ResponseEntity<?> giaHanvnpay(@RequestBody Map<String, Object> payload, HttpServletRequest request) {
        try {
            long duration = Long.parseLong(payload.get("duration").toString());
            double price = Double.parseDouble(payload.get("price").toString());
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            CustomUserDetail userDetail = (CustomUserDetail) auth.getPrincipal();
            Account account = userDetail.getAccount();
            Doctor doctor = doctorRepository.findByAccount(account);
            if (doctor == null) {
                return ResponseEntity.badRequest().body("Không tìm thấy bác sĩ tương ứng");
            }
            order_membership order_membership = new order_membership();
            order_membership.setMembership(doctor.getMembership());
            double totalprice = duration * price;
            long kq = (long) totalprice;
            String paymentUrl = vnPayService.createPaymentUrl(request, kq, "giahanMembership",
                    doctor.getMembership().getMembershipId().toString());
            return ResponseEntity.ok(Map.of("url", paymentUrl));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Lỗi server: " + e.getMessage());
        }
    }

    @PostMapping("/xulynangcapvnpay")
    public ResponseEntity<?> nangCapMembershipvnpay(@RequestBody Map<String, Object> payload,
            HttpServletRequest request) throws UnsupportedEncodingException {
        // Lấy giá trị membershipId từ payload JSON
        Object membershipIdObj = payload.get("membershipId");
        if (membershipIdObj == null) {
            return ResponseEntity.badRequest().body("membershipId is required");
        }
        Long membershipId;
        try {
            // Nếu payload gửi membershipId dưới dạng số hoặc chuỗi số, ta parse sang Long
            membershipId = Long.parseLong(membershipIdObj.toString());
            Memberships memberships = membershipsService.findbyid(membershipId);
            double price = memberships.getPrice();
            long kq = (long) price;
            order_membership order_membership = new order_membership();
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            CustomUserDetail userDetail = (CustomUserDetail) auth.getPrincipal();
            Account account = userDetail.getAccount();
            Doctor doctor = doctorRepository.findByAccount(account);
            String paymentUrl = vnPayService.createPaymentUrl(request, kq, "nangcapMembership",
                    membershipId.toString());

            return ResponseEntity.ok(Map.of("url", paymentUrl));
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body("membershipId must be a number");
        }

    }
}
