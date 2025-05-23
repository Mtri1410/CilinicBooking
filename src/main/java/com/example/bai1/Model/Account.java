package com.example.bai1.Model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "accounts")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "account_id")
    private Long accountId;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(nullable = false, unique = true, length = 50, name = "name_account")
    private String username;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(length = 255, name = "reset_password_token")
    private String resetPasswordToken;
    @Column(name = "reset_token_expiry")
    private LocalDateTime reset_token_expiry;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Role role;

    @Column(name = "created_at", updatable = false, columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime updatedAt = LocalDateTime.now();

    public enum Role {
        PATIENT, DOCTOR, ADMIN
    }

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private AuthProvider provider; // Enum: LOCAL, GOOGLE, FACEBOOK, v.v.

    public enum AuthProvider {
        LOCAL,
        GOOGLE,
        FACEBOOK
    }

}
