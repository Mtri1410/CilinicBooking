package com.example.bai1.Model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "memberships")
public class Memberships {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long membershipId;

    @Column(nullable = false, unique = true, length = 50)
    private String name;

    @Column(nullable = false)
    private Double price;

    @Column(length = 255)
    private String description;
    @OneToMany(mappedBy = "membership")
    @JsonManagedReference
    private List<order_membership> orders_membership;
    @Column(name = "created_at", updatable = false, columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime updatedAt = LocalDateTime.now();
}
