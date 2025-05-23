package com.example.bai1.Controller.User;

import com.example.bai1.Model.*;
import com.example.bai1.Repository.Doctor.DoctorRepository;
import com.example.bai1.Service.Doctor.SchedulesService;
import com.example.bai1.Service.Doctor.AppointmentsService;
import com.example.bai1.Service.Doctor.UserDoctorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/doctor")
public class DoctorBookingApiController {
    @Autowired
    private DoctorRepository doctorRepository;
    @Autowired
    private SchedulesService schedulesService;
    @Autowired
    private AppointmentsService appointmentsService;
    @Autowired
    private UserDoctorService userDoctorService;

    @GetMapping("/{doctorId}/available-slots")
    public ResponseEntity<List<String>> getAvailableSlots(
            @PathVariable Long doctorId,
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        Doctor doctor = doctorRepository.findByDoctorId(doctorId);
        List<Schedules> schedules = schedulesService.getSchedulesByDoctorAndDate(doctor, date, Sort.unsorted());
        List<String> availableSlots = new ArrayList<>();
        for (Schedules s : schedules) {
            if (s.getStatus() == Schedules.Status.AVAILABLE) {
                availableSlots.add(s.getStartTime() + "-" + s.getEndTime());
            }
        }
        return ResponseEntity.ok(availableSlots);
    }

    @GetMapping("/{doctorId}/booked-slots")
    public ResponseEntity<List<String>> getBookedSlots(
            @PathVariable Long doctorId,
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        Doctor doctor = doctorRepository.findByDoctorId(doctorId);
        List<Schedules> schedules = schedulesService.getSchedulesByDoctorAndDate(doctor, date, Sort.unsorted());
        List<String> bookedSlots = new ArrayList<>();
        for (Schedules s : schedules) {
            if (s.getStatus() == Schedules.Status.BOOKED) {
                bookedSlots.add(s.getStartTime() + "-" + s.getEndTime());
            }
        }
        return ResponseEntity.ok(bookedSlots);
    }

    // DTO cho booking request
    public static class BookingRequest {
        private LocalDate date;
        private LocalTime startTime;
        private LocalTime endTime;
        public LocalDate getDate() { return date; }
        public void setDate(LocalDate date) { this.date = date; }
        public LocalTime getStartTime() { return startTime; }
        public void setStartTime(LocalTime startTime) { this.startTime = startTime; }
        public LocalTime getEndTime() { return endTime; }
        public void setEndTime(LocalTime endTime) { this.endTime = endTime; }
    }

    @PostMapping("/{doctorId}/book-slot")
    public ResponseEntity<?> bookSlot(
            @PathVariable Long doctorId,
            @RequestBody BookingRequest request) {
        // Kiểm tra user đã đăng nhập chưa
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal().equals("anonymousUser")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Vui lòng đăng nhập để đặt lịch!");
        }

        // Lấy thông tin user
        CustomUserDetail userDetail = (CustomUserDetail) auth.getPrincipal();
        if (userDetail == null || userDetail.getAccount() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Không tìm thấy thông tin người dùng!");
        }

        // Lấy user từ account
        User user = userDoctorService.findByAccount(userDetail.getAccount());
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy thông tin bệnh nhân!");
        }

        Doctor doctor = doctorRepository.findByDoctorId(doctorId);
        if (doctor == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy thông tin bác sĩ!");
        }

        LocalDate date = request.getDate();
        LocalTime startTime = request.getStartTime();
        LocalTime endTime = request.getEndTime();

        // Tìm slot đã tồn tại chưa
        List<Schedules> schedules = schedulesService.getSchedulesByDoctorAndDate(doctor, date, Sort.unsorted());
        Schedules slot = null;
        for (Schedules s : schedules) {
            if (s.getStartTime().equals(startTime) && s.getEndTime().equals(endTime)) {
                slot = s;
                break;
            }
        }

        if (slot != null) {
            if (slot.getStatus() == Schedules.Status.BOOKED) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Slot đã được đặt!");
            }
            slot.setStatus(Schedules.Status.BOOKED);
        } else {
            slot = new Schedules();
            slot.setDoctor(doctor);
            slot.setDate(date);
            slot.setStartTime(startTime);
            slot.setEndTime(endTime);
            slot.setStatus(Schedules.Status.BOOKED);
        }

        try {
            schedulesService.save(slot);

            // Tạo appointment mới
            Appointments appointment = new Appointments();
            appointment.setPatient(user);
            appointment.setSchedule(slot);
            
            // Sử dụng ngày và giờ của lịch hẹn thực tế thay vì thời gian hiện tại
            LocalDateTime appointmentDateTime = LocalDateTime.of(date, startTime);
            appointment.setBookingTime(appointmentDateTime);
            
            appointment.setStatus(Appointments.Status.PENDING);
            appointment.setDepositAmount(500000.0); // Đặt cọc 500,000 VND

            // Tính period dựa vào thời gian
            int period = calculatePeriod(startTime);
            appointment.setPeriod(period);

            appointmentsService.save(appointment);

            return ResponseEntity.ok("Đặt lịch thành công!");
        } catch (Exception e) {
            // Rollback lại trạng thái của slot nếu có lỗi
            if (slot != null) {
                slot.setStatus(Schedules.Status.AVAILABLE);
                schedulesService.save(slot);
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Có lỗi xảy ra khi đặt lịch: " + e.getMessage());
        }
    }

    // Hàm tính period dựa vào thời gian bắt đầu
    private int calculatePeriod(LocalTime startTime) {
        // Định nghĩa các khoảng thời gian và period tương ứng
        if (startTime.equals(LocalTime.of(7, 0))) return 1;  // 7:00-8:30
        if (startTime.equals(LocalTime.of(8, 30))) return 2; // 8:30-10:00
        if (startTime.equals(LocalTime.of(10, 0))) return 3; // 10:00-11:30
        if (startTime.equals(LocalTime.of(13, 0))) return 4; // 13:00-14:30
        if (startTime.equals(LocalTime.of(14, 30))) return 5; // 14:30-16:00
        if (startTime.equals(LocalTime.of(16, 0))) return 6; // 16:00-17:30
        return 0; // Mặc định nếu không khớp với các khoảng thời gian trên
    }
} 