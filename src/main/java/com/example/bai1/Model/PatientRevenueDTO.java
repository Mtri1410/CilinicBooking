package com.example.bai1.Model;

import java.math.BigDecimal;
import java.util.Date;

public class PatientRevenueDTO {
    private String patientName;
    private Long totalQuantity;
    private BigDecimal totalRevenue;
    private Date orderDate;

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
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

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public PatientRevenueDTO(String patientName, Long totalQuantity, BigDecimal totalRevenue, Date orderDate) {
        this.patientName = patientName;
        this.totalQuantity = totalQuantity;
        this.totalRevenue = totalRevenue;
        this.orderDate = orderDate;
    }

    public PatientRevenueDTO() {
    }
}
