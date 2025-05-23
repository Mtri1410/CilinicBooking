package com.example.bai1.Repository.Doctor;

import com.example.bai1.Model.Account;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    Account findByUsername(String username);

    Account findByEmail(String email);

    Account findByResetPasswordToken(String reset_password_token);

    Page<Account> findByUsernameContainingIgnoreCase(String username, Pageable pageable);

    Page<Account> findByRole(Account.Role role, Pageable pageable);

    Page<Account> findByUsernameContainingIgnoreCaseAndRole(String username, Account.Role role, Pageable pageable);
}
