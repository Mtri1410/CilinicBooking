package com.example.bai1.Model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "schedules")
public class Schedules {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long scheduleId;

    @ManyToOne
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @Column(nullable = false)
    private java.time.LocalDate date;

    @Column(nullable = false)
    private java.time.LocalTime startTime;

    @Column(nullable = false)
    private java.time.LocalTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private Status status = Status.AVAILABLE;

    public enum Status {
        AVAILABLE, BOOKED
    }

    @Column(name = "created_at", updatable = false, columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt = LocalDateTime.now();

    // Thêm mối quan hệ one-to-many với appointments để kiểm soát cách xử lý cascade
    @OneToMany(mappedBy = "schedule", cascade = CascadeType.PERSIST)
    @JsonIgnore
    private List<Appointments> appointments;
}
