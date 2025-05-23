package com.example.bai1.Repository.Doctor;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.bai1.Model.Doctor;
import com.example.bai1.Model.Products;

@Repository
public interface ProductsRepository extends JpaRepository<Products, Long> {

    List<Products> findAll(Sort sort);

    List<Products> findByName(String name);

    Products findByProductId(Long productId);

    Products findTopByOrderByProductIdDesc();

    List<Products> findByDoctor_DoctorId(Long doctorId);

    Page<Products> findByDoctor_DoctorIdAndCountGreaterThan(Long doctorId, int count, Pageable pageable);

    Page<Products> findByDoctor_DoctorIdAndNameContainingIgnoreCaseAndCountGreaterThan(
            Long doctorId, String keyword, int count, Pageable pageable);

    boolean existsByNameAndDoctor(String name, Doctor doctor);

}
