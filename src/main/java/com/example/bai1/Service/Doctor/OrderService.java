package com.example.bai1.Service.Doctor;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.example.bai1.Model.Doctor;
import com.example.bai1.Model.Order_items;
import com.example.bai1.Model.Orders;
import com.example.bai1.Model.PatientRevenueDTO;
import com.example.bai1.Model.ProductRevenueDTO;
import com.example.bai1.Model.RevenueDTO;
import com.example.bai1.Repository.Doctor.OrderItemRepository;
import com.example.bai1.Repository.Doctor.OrderRepository;

@Service
public class OrderService {
    private final OrderRepository orderRepository;

    @Autowired
    public OrderService(OrderRepository order) {
        orderRepository = order;
    }

    public Orders getorderbyorderid(Long id) {
        return orderRepository.findByOrderId(id);
    }

    public List<Orders> getAll(List<Order_items> list) {
        return orderRepository.findDistinctByOrderItems(list);
    }

    public void save(Orders order) {
        orderRepository.save(order);
    }

    public Page<Orders> getOrdersByItems(List<Order_items> items, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("orderId").descending()); // hoặc sắp xếp khác
        return orderRepository.findDistinctByOrderItems(items, pageable);
    }

    public Page<Orders> getOrdersByItemsAndSearch(List<Order_items> items, String keyword, int page,
            int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("orderId").ascending());

        if (keyword != null && !keyword.trim().isEmpty()) {
            return orderRepository.searchByKeywordInStatusOrFullName(items, keyword, pageable);
        } else {
            return orderRepository.findDistinctByOrderItems(items, pageable);
        }
    }

    public List<Orders> getAlltemp() {
        return orderRepository.findAll();
    }

    public RevenueDTO getTotalRevenueByDoctor(Doctor doctor) {
        return orderRepository.getTotalRevenueByDoctor(doctor);
    }

    public List<PatientRevenueDTO> getRevenueChart(Doctor doctor,
            LocalDateTime startDate,
            LocalDateTime endDate) {
        return orderRepository.getRevenueByPatientAndDateRange(doctor, startDate,
                endDate);
    }

    public Page<PatientRevenueDTO> getRevenueTable(Doctor doctor, LocalDateTime startDate,
            LocalDateTime endDate, String keyword, int page,
            int size) {
        Pageable pageable = PageRequest.of(page, size);

        if (keyword != null && !keyword.trim().isEmpty()) {
            return orderRepository.getRevenueByPatientAndDateRangeWithKeyword(doctor, startDate, endDate, keyword,
                    pageable);
        } else {
            return orderRepository.getRevenueByPatientAndDateRange(doctor, startDate, endDate, pageable);
        }
    }

    public List<ProductRevenueDTO> getRevenueProductChart(Doctor doctor, LocalDateTime startDate,
            LocalDateTime endDate) {
        return orderRepository.getRevenueAndQuantityByProductAndDateRange(doctor, startDate, endDate);
    }

    public Page<ProductRevenueDTO> getRevenueProductTable(Doctor doctor, LocalDateTime startDate,
            LocalDateTime endDate, String keyword, int page,
            int size) {
        Pageable pageable = PageRequest.of(page, size);

        if (keyword != null && !keyword.trim().isEmpty()) {
            return orderRepository.getRevenueAndQuantityByProductAndDateRangeWithKeyword(doctor, startDate, endDate,
                    keyword,
                    pageable);
        } else {
            return orderRepository.getRevenueAndQuantityByProductAndDateRangeWithoutKeyword(doctor, startDate, endDate,
                    pageable);
        }
    }
}
