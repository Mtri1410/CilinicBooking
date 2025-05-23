package com.example.bai1.Service.Doctor;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.example.bai1.Model.Order_items;
import com.example.bai1.Model.Products;
import com.example.bai1.Repository.Doctor.MembershipsRepository;
import com.example.bai1.Repository.Doctor.OrderItemRepository;

@Service
public class OrderItemService {
    private final OrderItemRepository orderItemRepository;

    @Autowired
    public OrderItemService(OrderItemRepository orderitem) {
        orderItemRepository = orderitem;
    }

    public List<Order_items> getAll(Products products) {
        return orderItemRepository.findByProduct(products);
    }

    public List<Order_items> getAllByListProduct(List<Products> products) {
        return orderItemRepository.findByProductIn(products);
    }

    public Page<Order_items> findByOrderIdAndProductName(Long orderId, String keyword, int page,
            int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC,
                "order_item_id"));
        if (keyword == null || keyword.trim().isEmpty()) {
            return orderItemRepository.findByOrderId(orderId, pageable);
        }
        return orderItemRepository.searchByOrderIdAndProductName(orderId, keyword,
                pageable);
    }

    public Order_items save(Order_items item) {
        return orderItemRepository.save(item);
    }
}
