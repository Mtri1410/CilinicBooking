package com.example.bai1.Repository;

import com.example.bai1.Model.Reviews;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewsRepository extends JpaRepository<Reviews, Long> {
    // You can add custom query methods here if needed later
    // For example:
    // List<Reviews> findByDoctorId(Long doctorId);
    // List<Reviews> findByRating(Integer rating);
    // List<Reviews> findByStatus(String status); // If you add a status field
} 