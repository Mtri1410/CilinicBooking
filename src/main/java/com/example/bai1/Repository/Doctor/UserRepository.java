package com.example.bai1.Repository.Doctor;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.bai1.Model.Doctor;
import com.example.bai1.Model.User;
import com.example.bai1.Model.Account;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Query("SELECT u FROM User u WHERE u.account.email = :email")
    User findByAccountEmail(@Param("email") String email);

    @Query("SELECT u FROM User u WHERE u.account.username = :username")
    User findByAccountUsername(@Param("username") String username);

    // List<User> findByPhoneNumber(String phoneNumber);
    User findByPhoneNumber(String phoneNumber);

    User findByUserId(Long userId);
	User findByAccount(Account account);
	Page<User> findByFullNameContainingIgnoreCaseOrPhoneNumberContainingIgnoreCase(String fullName, String phoneNumber, Pageable pageable);
}
