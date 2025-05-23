package com.example.bai1.Service.Doctor;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.example.bai1.Model.Doctor;
import com.example.bai1.Model.Products;
import com.example.bai1.Repository.Doctor.AppointmentsRepository;
import com.example.bai1.Repository.Doctor.ProductsRepository;

@Service
public class ProductsService {
    private final ProductsRepository productsRepository;

    @Autowired
    public ProductsService(ProductsRepository pro) {
        productsRepository = pro;
    }

    public List<Products> getAllProducts() {
        return productsRepository.findAll();
    }

    public Products getProducts(Long id) {
        return productsRepository.findByProductId(id);
    }

    public Products getProductsidDesc() {
        return productsRepository.findTopByOrderByProductIdDesc();
    }

    public Products saveProduct(Products product) {
        return productsRepository.save(product);
    }

    public List<Products> getProductByDoctor(Long id) {
        return productsRepository.findByDoctor_DoctorId(id);
    }

    public Page<Products> getProductByDoctor(Long doctorId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("productId").ascending());
        // tùy ý
        return productsRepository.findByDoctor_DoctorIdAndCountGreaterThan(doctorId, 0, pageable);
    }

    public Page<Products> getProductByDoctorAndSearch(Long doctorId, String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("productId").ascending());
        if (keyword != null && !keyword.isEmpty()) {
            return productsRepository.findByDoctor_DoctorIdAndNameContainingIgnoreCaseAndCountGreaterThan(doctorId,
                    keyword, 0, pageable);
        } else {
            return productsRepository.findByDoctor_DoctorIdAndCountGreaterThan(doctorId, 0, pageable);
        }

    }

    public boolean existsByNameAndDoctor(String name, Doctor doctor) {
        return productsRepository.existsByNameAndDoctor(name, doctor);
    }
}
