package com.example.bai1.Controller.Doctor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.bai1.Model.Account;
import com.example.bai1.Model.Appointments;
import com.example.bai1.Model.CustomUserDetail;
import com.example.bai1.Model.Doctor;
import com.example.bai1.Model.Schedules;
import com.example.bai1.Model.User;
import com.example.bai1.Model.Appointments.Status;
import com.example.bai1.Repository.Doctor.DoctorRepository;
import com.example.bai1.Repository.Doctor.UserRepository;
import com.example.bai1.Service.VnPayService;
import com.example.bai1.Service.Doctor.AppointmentsService;
import com.example.bai1.Service.Doctor.SchedulesService;
import com.example.bai1.Service.Doctor.UserDoctorService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/doctor/appointments")
public class AppointmentsController {
    @Autowired
    private AppointmentsService appointmentService;
    @Autowired
    private SchedulesService schedulesService;
    @Autowired
    private UserDoctorService userDoctorService;
    @Autowired
    private DoctorRepository doctorRepository;
    @Autowired
    private VnPayService vnPayService;
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/confirmappointments")
    public ResponseEntity<Map<String, Object>> getAppointmentsByDoctorId(

            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "2") int pagesize, @RequestParam(required = false) String keyword) {

        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            CustomUserDetail userDetail = (CustomUserDetail) auth.getPrincipal();
            Account account = userDetail.getAccount();
            Doctor doctor = doctorRepository.findByAccount(account);
            if (doctor == null) {
                System.out.println("------------------------------------------------");
                throw new RuntimeException("Không tìm thấy bác sĩ tương ứng với tài khoản hiện tại");

            }
            Page<Appointments> appointmentsPage = appointmentService
                    .getAppointmentsByDoctorIdAndOptionalKeyword(doctor.getDoctorId(), keyword, page - 1, pagesize);

            Map<String, Object> response = new HashMap<>();
            response.put("Data", appointmentsPage.getContent()); // Dữ liệu
            response.put("TotalItems", appointmentsPage.getTotalElements()); // Tổng số bản ghi
            response.put("CurrentPage", appointmentsPage.getNumber() + 1); // Trang hiện tại (1-based)
            response.put("NumberPage", appointmentsPage.getTotalPages()); // Tổng số trang
            response.put("PageSize", pagesize);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Lỗi khi lấy danh sách lịch hẹn: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @PostMapping("/updateStatus")
    public ResponseEntity<?> updateAppointmentStatus(@RequestBody Map<String, Object> payload) {
        try {
            Long appointmentId = Long.valueOf(payload.get("appointmentId").toString());
            String status = payload.get("status").toString();

            Appointments appointment = appointmentService.getappointments(appointmentId);
            long scheduleid;
            if (appointment == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Appointment not found");
            }
            // Cập nhật trạng thái cuộc hẹn
            if ("CONFIRMED".equalsIgnoreCase(status)) {
                appointment.setStatus(Status.CONFIRMED);
                scheduleid = appointment.getSchedule().getScheduleId();
                Schedules schedule = schedulesService.findbyschedulesid(scheduleid);
                schedule.setStatus(com.example.bai1.Model.Schedules.Status.BOOKED);
                schedulesService.save(schedule);
            } else {
                appointment.setStatus(Status.CANCELLED);
            }

            appointmentService.save(appointment);

            return ResponseEntity.ok("Status updated");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating status");
        }
    }

    @GetMapping("/get")
    public ResponseEntity<?> getAppointments(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {

        // Kiểm tra tham số đầu vào
        if (startDate == null || endDate == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Vui lòng cung cấp startDate và endDate");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            CustomUserDetail userDetail = (CustomUserDetail) auth.getPrincipal();
            Account account = userDetail.getAccount();
            Doctor doctor = doctorRepository.findByAccount(account);
            LocalDateTime startDateTime = startDate.atStartOfDay(); // 00:00:00
            LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX); // 23:59:59.999999999
            // Lấy danh sách lịch hẹn từ service
            List<Appointments> appointments = appointmentService.getAppointmentsComfirm(doctor, Status.CONFIRMED,
                    startDateTime, endDateTime);

            // Trả về kết quả
            return new ResponseEntity<>(appointments, HttpStatus.OK);

        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Lỗi khi lấy dữ liệu lịch hẹn: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/searchbyphone")
    public ResponseEntity<Map<String, Object>> getUserByPhone(
            @RequestParam String phone) {

        try {
            User user = userDoctorService.findbyphone(phone);

            Map<String, Object> response = new HashMap<>();
            response.put("Data", user);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Lỗi khi lấy danh sách lịch hẹn: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @PostMapping("/VnPaySchedule")
    public ResponseEntity<Map<String, Object>> toVnPaySchedule(
            @RequestParam String date,
            @RequestParam String period,
            @RequestParam String id,
            @RequestParam String note,
            @RequestParam String ammout,
            HttpServletRequest request) {

        try {
            long idlong = Long.parseLong(id);
            long ammoutl = Long.parseLong(ammout);
            Map<String, Object> response = new HashMap<>();
            String paymentUrl = vnPayService.createPaymentUrl(request, ammoutl,
                    "pickschedule",
                    date, period, note, idlong);
            return ResponseEntity.ok(Map.of("url", paymentUrl));
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Lỗi khi lấy dữ liệu lịch hẹn: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);

        }
    }

    @PostMapping("/Addschedule")
    public ResponseEntity<?> addschedule(
            @RequestParam String date,
            @RequestParam String period,
            @RequestParam String id,
            @RequestParam String note,
            @RequestParam String ammout) {

        try {
            Integer pe = Integer.parseInt(period);
            long idlong = Long.parseLong(id);
            Double ammoutl = Double.parseDouble(ammout);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDate localDate = LocalDate.parse(date, formatter);
            String start = "09:00";
            String end = "10:00";
            switch (pe) {
                case 1:
                    start = "07:00";
                    end = "08:30";
                    break;
                case 2:
                    start = "08:30";
                    end = "10:00";
                    break;
                case 3:
                    start = "10:00";
                    end = "11:30";
                    break;
                case 4:
                    start = "13:00";
                    end = "14:30";
                    break;
                case 5:
                    start = "14:30";
                    end = "16:00";
                    break;

                case 6:
                    start = "16:00";
                    end = "17:30";
                    break;

                default:
                    start = "07:00";
                    end = "08:30";
                    break;
            }
            Schedules schedules = new Schedules();
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            CustomUserDetail userDetail = (CustomUserDetail) auth.getPrincipal();
            Account account = userDetail.getAccount();
            Doctor doctor = doctorRepository.findByAccount(account);
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
            LocalTime startTime = LocalTime.parse(start, timeFormatter);
            LocalTime endTime = LocalTime.parse(end, timeFormatter);
            LocalDateTime bookingTime = localDate.atStartOfDay();
            schedules.setDoctor(doctor);
            schedules.setDate(localDate);
            schedules.setStartTime(startTime);
            schedules.setEndTime(endTime);
            schedules.setStatus(com.example.bai1.Model.Schedules.Status.BOOKED);
            schedulesService.save(schedules);
            Appointments appointments = new Appointments();
            User user = userDoctorService.findbyid(idlong);
            user.setNote(note);
            user.setFollowup(user.getFollowup() + 1);
            userDoctorService.save(user);
            appointments.setPatient(user);
            appointments.setSchedule(schedules);
            appointments.setBookingTime(bookingTime);
            appointments.setStatus(Status.CONFIRMED);
            appointments.setDepositAmount(ammoutl);
            appointments.setPeriod(pe);
            appointmentService.save(appointments);
            String responseMessage = "Đã nhận membershipId: ";
            return ResponseEntity.ok(responseMessage);
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body("membershipId must be a number");
        }
    }

    @PostMapping("/{doctorId}/book-slot-vnpay")
    public ResponseEntity<?> bookSlotVnpay(@PathVariable Long doctorId, @RequestBody Map<String, String> req, jakarta.servlet.http.HttpServletRequest request) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated() || auth.getPrincipal().equals("anonymousUser")) {
                return ResponseEntity.status(401).body("Vui lòng đăng nhập để đặt lịch!");
            }
            String username = auth.getName();
            User user = userRepository.findByAccountUsername(username);
            if (user == null) user = userRepository.findByAccountEmail(username);
            if (user == null) return ResponseEntity.status(403).body("Không tìm thấy thông tin người dùng!");
            Doctor doctor = doctorRepository.findById(doctorId).orElse(null);
            if (doctor == null) return ResponseEntity.badRequest().body("Không tìm thấy bác sĩ!");
            String date = req.get("date");
            String startTime = req.get("startTime");
            String endTime = req.get("endTime");
            // Tạo mới Schedules
            java.time.LocalDate localDate = java.time.LocalDate.parse(date);
            java.time.LocalTime localStart = java.time.LocalTime.parse(startTime);
            java.time.LocalTime localEnd = java.time.LocalTime.parse(endTime);
            Schedules schedule = new Schedules();
            schedule.setDoctor(doctor);
            schedule.setDate(localDate);
            schedule.setStartTime(localStart);
            schedule.setEndTime(localEnd);
            schedule.setStatus(Schedules.Status.BOOKED);
            schedulesService.save(schedule);
            // Tạo appointment trạng thái PENDING
            Appointments appt = new Appointments();
            appt.setPatient(user);
            appt.setSchedule(schedule);
            appt.setBookingTime(java.time.LocalDateTime.now());
            appt.setStatus(Appointments.Status.PENDING);
            appt.setDepositAmount(500000.0);
            appt.setPeriod(0); // hoặc xác định period nếu cần
            appointmentService.save(appt);
            // Tạo URL QR code VNPAY (giả sử đặt cọc 500000)
            String paymentUrl = vnPayService.createPaymentUrl(request, 500000, "pickschedule", String.valueOf(appt.getAppointmentId()));
            return ResponseEntity.ok(Map.of("url", paymentUrl));
        } catch (java.io.UnsupportedEncodingException e) {
            return ResponseEntity.status(500).body("Lỗi encoding khi tạo URL thanh toán!");
        }
    }

}
