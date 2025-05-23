package com.example.bai1.Model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Products {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;

    @ManyToOne
    @JoinColumn(name = "doctor_id", nullable = false)
    @JsonBackReference
    private Doctor doctor;

    @Column(nullable = false, unique = true, length = 255)
    private String name;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "count")
    private Long count;

    @Column(name = "description")
    private String description;

    @Column(length = 255)
    private String image;

    @Column(name = "created_at", updatable = false, columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();

    @JsonManagedReference
    @OneToMany(mappedBy = "product")
    private List<Carts> carts;

    // @JsonManagedReference
    // @OneToMany(mappedBy = "product")
    // private List<Order_items> orderItems;
}
