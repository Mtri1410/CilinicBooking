package com.example.bai1.Repository.Doctor;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.bai1.Model.Order_items;
import com.example.bai1.Model.Products;

@Repository
public interface OrderItemRepository extends JpaRepository<Order_items, Long> {
    List<Order_items> findByProduct(Products product);

    List<Order_items> findByProductIn(List<Products> product);

    @Query(value = "SELECT oi.* FROM order_items oi " +
            "JOIN products p ON oi.product_id = p.product_id " +
            "WHERE oi.order_id = :orderId", countQuery = "SELECT COUNT(*) FROM order_items oi " +
                    "JOIN products p ON oi.product_id = p.product_id " +
                    "WHERE oi.order_id = :orderId", nativeQuery = true)
    Page<Order_items> findByOrderId(@Param("orderId") Long orderId, Pageable pageable);

    @Query(value = "SELECT oi.* FROM order_items oi " +
            "JOIN products p ON oi.product_id = p.product_id " +
            "WHERE oi.order_id = :orderId " +
            "AND LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%'))", countQuery = "SELECT COUNT(*) FROM order_items oi "
                    +
                    "JOIN products p ON oi.product_id = p.product_id " +
                    "WHERE oi.order_id = :orderId " +
                    "AND LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%'))", nativeQuery = true)
    Page<Order_items> searchByOrderIdAndProductName(@Param("orderId") Long orderId,
            @Param("keyword") String keyword,
            Pageable pageable);
}
