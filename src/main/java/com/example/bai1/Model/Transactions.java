package com.example.bai1.Model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transactions {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long transactionId;

    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(length = 10, nullable = false)
    private String paymentMethod; // MOMO, VNPAY

    @Column(length = 15, nullable = false)
    private String transactionType; // APPOINTMENT, ORDER, MEMBERSHIP

    @Column(length = 10)
    @Builder.Default
    private String status = "PENDING"; // PENDING, SUCCESS, FAILED
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
