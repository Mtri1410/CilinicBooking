package com.example.bai1.Model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "appointments")
public class Appointments {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long appointmentId;

    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    // @JsonBackReference
    private User patient;
    @Column(nullable = false, name = "period")
    private int period;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "schedule_id", nullable = false)
    // @JsonIgnoreProperties({ "appointments", "doctor.orders_membership",
    // "doctor.membership.orders_membership" })
    private Schedules schedule;

    @Column(nullable = false)
    private LocalDateTime bookingTime = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private Status status = Status.PENDING;

    public enum Status {
        PENDING, CONFIRMED, CANCELLED, PAID
    }

    @Column(name = "deposit_amount")
    private Double depositAmount;

    @Column(name = "created_at", updatable = false, columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime updatedAt = LocalDateTime.now();
}
