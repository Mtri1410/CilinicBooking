package com.example.bai1.Model;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;

public class RevenueDTO {
    private BigDecimal totalRevenue;
    private LocalDate date; // Có thể null nếu không thống kê theo ngày

    // Constructor cho thống kê không có ngày
    public RevenueDTO(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
        this.date = null;
    }

    public RevenueDTO(BigDecimal totalRevenue, Date date) {
        this.totalRevenue = totalRevenue;
        this.date = date != null ? date.toLocalDate() : null;
    }

    // Getter và Setter
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
