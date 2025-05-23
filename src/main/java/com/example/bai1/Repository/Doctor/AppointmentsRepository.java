package com.example.bai1.Repository.Doctor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.bai1.Model.Appointments;
import com.example.bai1.Model.Doctor;
import com.example.bai1.Model.Schedules;
import com.example.bai1.Model.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentsRepository extends JpaRepository<Appointments, Long> {
        List<Appointments> findBySchedule(Schedules schedule);

        // @Query(value = "SELECT a.* FROM appointments a " +
        // "JOIN schedules s ON a.schedule_id = s.schedule_id " +
        // "JOIN doctors d ON s.doctor_id = d.doctor_id " +
        // "JOIN users p ON a.patient_id = p.user_id " +
        // "WHERE d.doctor_id = :doctorId " +
        // "AND (p.full_name COLLATE utf8mb4_unicode_ci LIKE CONCAT('%', :keyword, '%')
        // " +
        // "OR DATE_FORMAT(a.booking_time, '%Y-%m-%d') LIKE CONCAT('%', :keyword,
        // '%'))", countQuery = "SELECT COUNT(*) FROM appointments a "
        // +
        // "JOIN schedules s ON a.schedule_id = s.schedule_id " +
        // "JOIN doctors d ON s.doctor_id = d.doctor_id " +
        // "JOIN users p ON a.patient_id = p.user_id " +
        // "WHERE d.doctor_id = :doctorId " +
        // "AND (p.full_name COLLATE utf8mb4_unicode_ci LIKE CONCAT('%', :keyword, '%')
        // " +
        // "OR DATE_FORMAT(a.booking_time, '%Y-%m-%d') LIKE CONCAT('%', :keyword,
        // '%'))", nativeQuery = true)
        // Page<Appointments> findByDoctorIdAndKeywordNative(
        // @Param("doctorId") Long doctorId,
        // @Param("keyword") String keyword,
        // Pageable pageable);

        @Query(value = "SELECT a.* FROM appointments a " +
                        "JOIN schedules s ON a.schedule_id = s.schedule_id " +
                        "JOIN doctors d ON s.doctor_id = d.doctor_id " +
                        "JOIN users p ON a.patient_id = p.user_id " +
                        "WHERE d.doctor_id = :doctorId " +
                        "AND LOWER(p.full_name) LIKE LOWER(CONCAT('%', :keyword, '%'))", countQuery = "SELECT COUNT(*) FROM appointments a "
                                        +
                                        "JOIN schedules s ON a.schedule_id = s.schedule_id " +
                                        "JOIN doctors d ON s.doctor_id = d.doctor_id " +
                                        "JOIN users p ON a.patient_id = p.user_id " +
                                        "WHERE d.doctor_id = :doctorId " +
                                        "AND LOWER(p.full_name) LIKE LOWER(CONCAT('%', :keyword, '%'))", nativeQuery = true)
        Page<Appointments> findByDoctorIdAndPatientName(
                        @Param("doctorId") Long doctorId,
                        @Param("keyword") String keyword,
                        Pageable pageable);

        // Search theo ngày (MySQL)
        @Query(value = "SELECT a.* FROM appointments a " +
                        "JOIN schedules s ON a.schedule_id = s.schedule_id " +
                        "JOIN doctors d ON s.doctor_id = d.doctor_id " +
                        "WHERE d.doctor_id = :doctorId " +
                        "AND DATE(a.booking_time) = :date", countQuery = "SELECT COUNT(*) FROM appointments a " +
                                        "JOIN schedules s ON a.schedule_id = s.schedule_id " +
                                        "JOIN doctors d ON s.doctor_id = d.doctor_id " +
                                        "WHERE d.doctor_id = :doctorId " +
                                        "AND DATE(a.booking_time) = :date", nativeQuery = true)
        Page<Appointments> findByDoctorIdAndBookingDate(
                        @Param("doctorId") Long doctorId,
                        @Param("date") LocalDate date,
                        Pageable pageable);

        @Query(value = "SELECT a.* FROM appointments a " +
                        "JOIN schedules s ON a.schedule_id = s.schedule_id " +
                        "JOIN doctors d ON s.doctor_id = d.doctor_id " +
                        "WHERE d.doctor_id = :doctorId", countQuery = "SELECT COUNT(*) FROM appointments a " +
                                        "JOIN schedules s ON a.schedule_id = s.schedule_id " +
                                        "JOIN doctors d ON s.doctor_id = d.doctor_id " +
                                        "WHERE d.doctor_id = :doctorId", nativeQuery = true)
        Page<Appointments> findByDoctorIdNative(
                        @Param("doctorId") Long doctorId,
                        Pageable pageable);

        Appointments findByAppointmentId(Long appointmentId);

        List<Appointments> findByPatientOrderByBookingTimeDesc(User patient);

        @Query("SELECT a FROM Appointments a " +
                        "WHERE a.schedule.doctor = :doctor " +
                        "AND a.status = :status " +
                        "AND a.bookingTime BETWEEN :start AND :end")
        List<Appointments> findByDoctorAndStatusAndBookingTimeBetween(
                        @Param("doctor") Doctor doctor,
                        @Param("status") Appointments.Status status,
                        @Param("start") LocalDateTime start,
                        @Param("end") LocalDateTime end);

}
