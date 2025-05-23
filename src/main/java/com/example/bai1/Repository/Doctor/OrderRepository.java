package com.example.bai1.Repository.Doctor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.bai1.Model.Orders;
import com.example.bai1.Model.PatientRevenueDTO;
import com.example.bai1.Model.ProductRevenueDTO;
import com.example.bai1.Model.Products;
import com.example.bai1.Model.RevenueDTO;
import com.example.bai1.Model.Doctor;
import com.example.bai1.Model.Order_items;

@Repository
public interface OrderRepository extends JpaRepository<Orders, Long> {
    List<Orders> findAll(Sort sort);

    Orders findByOrderId(Long orderId);

    @Query("SELECT DISTINCT o FROM Orders o JOIN o.orderItems oi WHERE oi IN :orderItems")
    List<Orders> findDistinctByOrderItems(@Param("orderItems") List<Order_items> orderItems);

    @Query("SELECT DISTINCT o FROM Orders o JOIN o.orderItems oi WHERE oi IN :orderItems")
    Page<Orders> findDistinctByOrderItems(@Param("orderItems") List<Order_items> orderItems, Pageable pageable);

    @Query("SELECT DISTINCT o FROM Orders o " +
            "JOIN o.orderItems oi " +
            "JOIN o.patient p " +
            "WHERE oi IN :orderItems AND " +
            "(" +
            "LOWER(o.status) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.fullName) LIKE LOWER(CONCAT('%', :keyword, '%'))" +
            ")")
    Page<Orders> searchByKeywordInStatusOrFullName(
            @Param("orderItems") List<Order_items> orderItems,
            @Param("keyword") String keyword,
            Pageable pageable);

    // thong ke thuong
    @Query("""
            SELECT new com.example.bai1.Model.RevenueDTO(
            SUM(oi.price * oi.quantity)
            )
            FROM Order_items oi
            WHERE oi.product.doctor = :doctor
            """)
    RevenueDTO getTotalRevenueByDoctor(@Param("doctor") Doctor doctor);

    // thong ke theo khoang ngay
    // @Query("""
    // SELECT new com.example.bai1.Model.RevenueDTO(
    // SUM(oi.price * oi.quantity),
    // CAST(function('DATE', oi.order.orderDate) AS java.sql.Date)
    // )
    // FROM Order_items oi
    // WHERE oi.product.doctor = :doctor
    // AND oi.order.orderDate BETWEEN :startDate AND :endDate
    // GROUP BY function('DATE', oi.order.orderDate)
    // ORDER BY function('DATE', oi.order.orderDate)
    // """)
    // List<RevenueDTO> getDailyRevenueByDoctorAndDateRange(
    // @Param("doctor") Doctor doctor,
    // @Param("startDate") LocalDateTime startDate,
    // @Param("endDate") LocalDateTime endDate);

    // thống kê thường theo khoảng ngày dạng biểu đồ
    @Query("""
            SELECT new com.example.bai1.Model.PatientRevenueDTO(
            oi.order.patient.fullName,
            CAST(SUM(oi.quantity) AS long),
            CAST(SUM(oi.price * oi.quantity) AS BigDecimal),
            CAST(oi.order.orderDate AS date)
            )
            FROM Order_items oi
            WHERE oi.product.doctor = :doctor
            AND oi.order.orderDate BETWEEN :startDate AND :endDate
            AND oi.order.status = 'PAID'
            GROUP BY oi.order.patient.fullName, CAST(oi.order.orderDate AS date)
            ORDER BY SUM(oi.price * oi.quantity) DESC
            """)
    List<PatientRevenueDTO> getRevenueByPatientAndDateRange(
            @Param("doctor") Doctor doctor,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    // thống kê thường theo khoảng ngày dạng bảng có tìm kiếm
    @Query("""
            SELECT new com.example.bai1.Model.PatientRevenueDTO(
                oi.order.patient.fullName,
                CAST(SUM(oi.quantity) AS long),
                CAST(SUM(oi.price * oi.quantity) AS BigDecimal),
                CAST( oi.order.orderDate AS date)
            )
            FROM Order_items oi
            WHERE oi.product.doctor = :doctor
            AND oi.order.orderDate BETWEEN :startDate AND :endDate
            AND LOWER(oi.order.patient.fullName) LIKE LOWER(CONCAT('%', :keyword, '%'))
            AND oi.order.status = 'PAID'
            GROUP BY oi.order.patient.fullName, CAST(oi.order.orderDate AS date)
            ORDER BY SUM(oi.price * oi.quantity) DESC
            """)
    Page<PatientRevenueDTO> getRevenueByPatientAndDateRangeWithKeyword(
            @Param("doctor") Doctor doctor,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("keyword") String keyword,
            Pageable pageable);

    // thống kê thường theo khoảng ngày dạng bảng không có tìm kiếm
    @Query("""
                SELECT new com.example.bai1.Model.PatientRevenueDTO(
                    oi.order.patient.fullName,
                    CAST(SUM(oi.quantity) AS long),
                    CAST(SUM(oi.price * oi.quantity) AS BigDecimal),
                    CAST(oi.order.orderDate AS date)
                )
                FROM Order_items oi
                WHERE oi.product.doctor = :doctor
                AND oi.order.orderDate BETWEEN :startDate AND :endDate
                AND oi.order.status = 'PAID'
                GROUP BY oi.order.patient.fullName,CAST(oi.order.orderDate AS date)
                ORDER BY SUM(oi.price * oi.quantity) DESC
            """)
    Page<PatientRevenueDTO> getRevenueByPatientAndDateRange(
            @Param("doctor") Doctor doctor,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);

    // thong ke theo san pham so luong
    @Query("""
            SELECT new com.example.bai1.Model.ProductRevenueDTO(
            oi.product.name,
            SUM(oi.quantity),
            SUM(oi.price * oi.quantity)
            )
            FROM Order_items oi
            WHERE oi.product.doctor = :doctor
            AND oi.order.status = 'PAID'
            GROUP BY oi.product.name
            ORDER BY SUM(oi.price * oi.quantity) DESC
            """)
    List<ProductRevenueDTO> getRevenueAndQuantityByProduct(
            @Param("doctor") Doctor doctor);

    // // thong ke san pham so luong theo khoang ngay dạng biểu đồ

    @Query("""
            SELECT new com.example.bai1.Model.ProductRevenueDTO(
            oi.product.name,
            CAST(SUM(oi.quantity) AS LONG),
            CAST(SUM(oi.price * oi.quantity) AS BigDecimal),
            CAST(oi.order.orderDate AS date)
            )
            FROM Order_items oi
            WHERE oi.product.doctor = :doctor
            AND oi.order.orderDate BETWEEN :startDate AND :endDate
            AND oi.order.status = 'PAID'
            GROUP BY oi.product.name,CAST(oi.order.orderDate AS date)
            ORDER BY SUM(oi.price * oi.quantity) DESC
            """)
    List<ProductRevenueDTO> getRevenueAndQuantityByProductAndDateRange(
            @Param("doctor") Doctor doctor,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    // thống kê sản phẩm theo trang dạng bảng có tìm kiếm
    @Query("""
            SELECT new com.example.bai1.Model.ProductRevenueDTO(
                oi.product.name,
                CAST(SUM(oi.quantity) AS long),
                CAST(SUM(oi.price * oi.quantity) AS BigDecimal),
                CAST(oi.order.orderDate AS date)
            )
            FROM Order_items oi
            WHERE oi.product.doctor = :doctor
            AND oi.order.orderDate BETWEEN :startDate AND :endDate
            AND LOWER(oi.product.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
            AND oi.order.status = 'PAID'
            GROUP BY oi.product.name,CAST(oi.order.orderDate AS date)
            ORDER BY SUM(oi.price * oi.quantity) DESC
            """)
    Page<ProductRevenueDTO> getRevenueAndQuantityByProductAndDateRangeWithKeyword(
            @Param("doctor") Doctor doctor,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("keyword") String keyword,
            Pageable pageable);

    // thống kê sản phâm theo trang dạng bảng không có tìm kiếm
    @Query("""
            SELECT new com.example.bai1.Model.ProductRevenueDTO(
            oi.product.name,
            CAST(SUM(oi.quantity) AS long),
            CAST(SUM(oi.price * oi.quantity) AS java.math.BigDecimal),
            CAST(oi.order.orderDate AS date)
            )
            FROM Order_items oi
            WHERE oi.product.doctor = :doctor
            AND oi.order.orderDate BETWEEN :startDate AND :endDate
            AND oi.order.status = 'PAID'
            GROUP BY oi.product.name,CAST(oi.order.orderDate AS date)
            ORDER BY SUM(oi.price * oi.quantity) DESC
            """)
    Page<ProductRevenueDTO> getRevenueAndQuantityByProductAndDateRangeWithoutKeyword(
            @Param("doctor") Doctor doctor,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);
}
