package com.example.bai1.Model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Table(name = "carts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Carts {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cartId;

    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private User patient;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    @JsonBackReference
    private Products product;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "created_at", updatable = false, columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
