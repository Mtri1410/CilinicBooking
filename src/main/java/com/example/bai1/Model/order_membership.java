package com.example.bai1.Model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "order_membership")
public class order_membership {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "doctor_id", nullable = false)
    @JsonBackReference
    private Doctor doctor;
    @ManyToOne
    @JoinColumn(name = "membership_id", nullable = false)
    @JsonBackReference
    private Memberships membership;
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalprice;
    @Column(name = "count")
    private Long count;
    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private Status status = Status.PENDING;
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private OrderType type;
    

    public enum Status {
        PENDING, APPROVED, REJECTED
    }

    public enum OrderType {
        RENEWAL, UPGRADE
    }

    @Column(name = "date", updatable = false, columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt = LocalDateTime.now();
}
