package com.example.bai1.dto;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class AppointmentBookingDTO {
    @NotNull(message = "ID bác sĩ không được để trống")
    private Long doctorId;

    @NotNull(message = "Ngày khám không được để trống")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate appointmentDate;

    @NotNull(message = "Thời gian khám không được để trống")
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime appointmentTime;

    private String note;
} 