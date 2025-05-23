package com.example.bai1.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "doctors")
public class Doctor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long doctorId;

    @OneToOne
    @JoinColumn(name = "account_id", nullable = false, unique = true)
    private Account account;

    @NotBlank(message = "Chuyên khoa không được để trống")
    @Size(max = 100, message = "Chuyên khoa không được vượt quá 100 ký tự")
    @Column(nullable = false, length = 100)
    private String specialty;

    @Size(max = 255, message = "Địa chỉ không được vượt quá 255 ký tự")
    @Column(nullable = true, length = 255)
    private String address;

    @NotBlank(message = "Số giấy phép hành nghề không được để trống")
    @Size(max = 100, message = "Số giấy phép không được vượt quá 100 ký tự")
    @Column(nullable = false, unique = true, length = 100)
    private String license;

    @NotBlank(message = "Họ và tên không được để trống")
    @Size(max = 250, message = "Họ và tên không được vượt quá 250 ký tự")
    @Column(nullable = false, length = 250)
    private String fullname;

    @NotBlank(message = "Giới tính không được để trống")
    @Size(max = 50, message = "Giới tính không được vượt quá 50 ký tự")
    @Column(nullable = false, length = 50)
    private String gender;
    @Column(nullable = false, unique = true, length = 20)
    private String phoneNumber;
    @NotNull(message = "Ngày sinh không được để trống")
    @Column(name = "birthday")
    private LocalDate birthday;
    @Column(length = 255)
    private String image;
    @Column(columnDefinition = "TEXT")
    private String experience;

    @ManyToOne
    @JoinColumn(name = "membership_id")
    private Memberships membership;

    @OneToMany(mappedBy = "doctor")
    @JsonManagedReference
    private List<order_membership> orders_membership;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private Status status = Status.PENDING;

    public enum Status {
        PENDING, APPROVED, REJECTED
    }

    @Column(name = "created_at", updatable = false, columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Column(name = "startdate")
    private LocalDate startdate;

    @Column(name = "enddate")
    private LocalDate enddate;
}
