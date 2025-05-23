package com.example.bai1.Model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reviews")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reviews {
    public enum ReviewStatus {
        PENDING, APPROVED, REJECTED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reviewId;

    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private User patient;

    @ManyToOne
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @Column(nullable = false)
    private Integer rating; // 1 đến 5

    @Column(columnDefinition = "TEXT")
    private String comment;
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    @Builder.Default
    private ReviewStatus status = ReviewStatus.PENDING; // Default to PENDING

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
