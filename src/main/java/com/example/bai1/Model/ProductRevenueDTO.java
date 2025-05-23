package com.example.bai1.Model;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;

public class ProductRevenueDTO {
    private String productName;
    private Long totalQuantity;
    private BigDecimal totalRevenue;
    private LocalDate date; // Trường ngày có thể null nếu không có khoảng ngày

    public ProductRevenueDTO(String productName, Long totalQuantity, BigDecimal totalRevenue, Date date) {
        this.productName = productName;
        this.totalQuantity = totalQuantity;
        this.totalRevenue = totalRevenue;
        this.date = date != null ? date.toLocalDate() : null;
    }

    // Constructor không có ngày (trường hợp không có khoảng ngày)
    public ProductRevenueDTO(String productName, Long totalQuantity, BigDecimal totalRevenue) {
        this.productName = productName;
        this.totalQuantity = totalQuantity;
        this.totalRevenue = totalRevenue;
        this.date = null;
    }

    // Getter và Setter
    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Long getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(Long totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

}
