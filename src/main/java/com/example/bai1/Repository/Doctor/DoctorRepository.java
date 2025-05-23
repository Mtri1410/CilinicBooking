package com.example.bai1.Repository.Doctor;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.bai1.Model.Account;
import com.example.bai1.Model.Doctor;
import java.util.List;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    Doctor findByAccount(Account account);

    Doctor findByDoctorId(Long doctorId);

    Page<Doctor> findByFullnameContainingIgnoreCase(String fullname, Pageable pageable);

    Page<Doctor> findByStatus(Doctor.Status status, Pageable pageable);

    Page<Doctor> findByFullnameContainingIgnoreCaseAndStatus(String fullname, Doctor.Status status, Pageable pageable);
}
