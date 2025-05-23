package com.example.bai1.Service.Doctor;

import com.example.bai1.Model.Appointments;
import com.example.bai1.Model.Doctor;
import com.example.bai1.Model.Schedules;
import com.example.bai1.Model.User;
import com.example.bai1.Model.Appointments.Status;
import com.example.bai1.Repository.Doctor.AppointmentsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;

@Service
public class AppointmentsService {
    private final AppointmentsRepository appointmentsRepository;
    private final SchedulesService schedulesService;

    @Autowired
    public AppointmentsService(AppointmentsRepository app, SchedulesService schedulesService) {
        this.appointmentsRepository = app;
        this.schedulesService = schedulesService;
    }

    public void save(Appointments appointments) {
        appointmentsRepository.save(appointments);
    }

    public List<Appointments> getAppointmentsComfirm(Doctor doctor, Status status, LocalDateTime start,
            LocalDateTime end) {
        return appointmentsRepository.findByDoctorAndStatusAndBookingTimeBetween(doctor, status, start, end);
    }

    public Appointments getappointments(Long id) {
        return appointmentsRepository.findByAppointmentId(id);
    }

    public List<Appointments> getAppointmentsByUser(User user) {
        return appointmentsRepository.findByPatientOrderByBookingTimeDesc(user);
    }

    @Transactional
    public boolean cancelAppointment(Long appointmentId, User user) {
        Optional<Appointments> appointmentOpt = appointmentsRepository.findById(appointmentId);

        if (appointmentOpt.isPresent()) {
            Appointments appointment = appointmentOpt.get();

            // Kiểm tra xem appointment có phải của user này không
            if (!appointment.getPatient().getUserId().equals(user.getUserId())) {
                System.out.println("Không thể hủy: Lịch hẹn không thuộc về user này");
                return false;
            }

            // Kiểm tra xem có thể hủy không (chỉ hủy được trạng thái PENDING và CONFIRMED)
            if (appointment.getStatus() != Status.PENDING && appointment.getStatus() != Status.CONFIRMED) {
                System.out.println("Không thể hủy: Lịch hẹn đã bị hủy hoặc đã hoàn thành");
                return false;
            }

            // Kiểm tra thời gian
            LocalDateTime appointmentTime = appointment.getBookingTime();
            LocalDateTime now = LocalDateTime.now();

            // Không thể hủy lịch hẹn đã qua
            if (appointmentTime.isBefore(now)) {
                System.out.println("Không thể hủy: Lịch hẹn đã qua");
                return false;
            }

            // Cập nhật trạng thái
            appointment.setStatus(Status.CANCELLED);
            appointment.setUpdatedAt(LocalDateTime.now());
            
            try {
                // Lưu appointment trước tiên
                appointmentsRepository.save(appointment);
                
                // Sau đó cập nhật trạng thái của schedule
                Schedules schedule = appointment.getSchedule();
                schedule.setStatus(Schedules.Status.AVAILABLE);
                schedulesService.save(schedule);
                
                System.out.println("Hủy lịch hẹn thành công");
                return true;
            } catch (Exception e) {
                System.out.println("Lỗi khi hủy lịch hẹn: " + e.getMessage());
                e.printStackTrace();
                return false;
            }
        }

        System.out.println("Không tìm thấy lịch hẹn với ID: " + appointmentId);
        return false;
    }

    @Transactional
    public boolean rescheduleAppointment(Long appointmentId, User user, String newDate, String newTime) {
        // Kiểm tra tham số đầu vào
        if (appointmentId == null || user == null || newDate == null || newTime == null) {
            System.out.println("Thay đổi lịch thất bại: Thiếu thông tin cần thiết");
            return false;
        }
        
        Optional<Appointments> appointmentOpt = appointmentsRepository.findById(appointmentId);

        if (appointmentOpt.isPresent()) {
            Appointments appointment = appointmentOpt.get();

            // Kiểm tra xem appointment có phải của user này không
            if (!appointment.getPatient().getUserId().equals(user.getUserId())) {
                System.out.println("Thay đổi lịch thất bại: Lịch hẹn không thuộc về user này");
                return false;
            }

            // Kiểm tra trạng thái
            if (appointment.getStatus() != Appointments.Status.PENDING
                    && appointment.getStatus() != Appointments.Status.CONFIRMED) {
                System.out.println("Thay đổi lịch thất bại: Chỉ có thể thay đổi lịch hẹn có trạng thái PENDING hoặc CONFIRMED");
                return false;
            }

            // Parse ngày giờ mới
            try {
                // Cố gắng parse thời gian từ period
                int period = Integer.parseInt(newTime);
                
                // Cố gắng parse ngày
                LocalDate date = LocalDate.parse(newDate);
                LocalDateTime newDateTime = date.atStartOfDay();
                
                // Kiểm tra thời gian mới có hợp lệ không
                LocalDateTime now = LocalDateTime.now();
                if (date.isBefore(now.toLocalDate())) {
                    System.out.println("Thay đổi lịch thất bại: Không thể đặt lịch vào ngày đã qua");
                    return false;
                }

                // Cập nhật thời gian và period mới
                appointment.setBookingTime(newDateTime);
                appointment.setPeriod(period);
                appointment.setUpdatedAt(LocalDateTime.now());
                appointmentsRepository.save(appointment);
                
                System.out.println("Thay đổi lịch thành công: " + appointment.getAppointmentId());
                return true;
            } catch (NumberFormatException e) {
                System.out.println("Thay đổi lịch thất bại: Period không hợp lệ - " + e.getMessage());
                return false;
            } catch (DateTimeParseException e) {
                System.out.println("Thay đổi lịch thất bại: Ngày không hợp lệ - " + e.getMessage());
                return false;
            } catch (Exception e) {
                System.out.println("Thay đổi lịch thất bại: " + e.getMessage());
                return false;
            }
        }

        System.out.println("Thay đổi lịch thất bại: Không tìm thấy lịch hẹn với ID: " + appointmentId);
        return false;
    }

    // public Page<Appointments> getAppointmentsByDoctorIdAndOptionalKeyword(Long
    // doctorid, String keyword,
    // int page,
    // int size) {
    // Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC,
    // "appointment_Id"));
    // if (keyword != null && !keyword.trim().isEmpty()) {
    // return appointmentsRepository.findByDoctorIdAndKeywordNative(doctorid,
    // keyword.trim(), pageable);
    // } else {
    // return appointmentsRepository.findByDoctorIdNative(doctorid, pageable);
    // }
    // }

    public Page<Appointments> getAppointmentsByDoctorIdAndOptionalKeyword(Long doctorId, String keyword, int page,
            int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("appointment_Id").ascending());

        if (keyword == null || keyword.trim().isEmpty()) {
            return appointmentsRepository.findByDoctorIdNative(doctorId, pageable);
        }

        keyword = keyword.trim();

        // Kiểm tra xem keyword có phải là date không
        try {
            // Thử parse các format date phổ biến
            LocalDate date = null;
            if (keyword.matches("\\d{4}-\\d{2}-\\d{2}")) {
                date = LocalDate.parse(keyword, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            } else if (keyword.matches("\\d{2}/\\d{2}/\\d{4}")) {
                date = LocalDate.parse(keyword, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            } else if (keyword.matches("\\d{4}")) {
                // Chỉ năm, search theo tên thôi
                return appointmentsRepository.findByDoctorIdAndPatientName(doctorId, keyword, pageable);
            }

            if (date != null) {
                return appointmentsRepository.findByDoctorIdAndBookingDate(doctorId, date, pageable);
            }
        } catch (DateTimeParseException e) {
            // Không phải date format, tiếp tục search theo tên
        }

        // Search theo tên
        return appointmentsRepository.findByDoctorIdAndPatientName(doctorId, keyword, pageable);
    }

    public Appointments getAppointmentById(long id) {
        return appointmentsRepository.findById(id).orElse(null);
    }
}
