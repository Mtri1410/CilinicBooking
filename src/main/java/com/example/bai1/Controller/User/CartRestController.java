package com.example.bai1.Controller.User;

import com.example.bai1.Model.*;
import com.example.bai1.Repository.Doctor.ProductsRepository;
import com.example.bai1.Repository.Doctor.UserRepository;
import com.example.bai1.Service.Doctor.OrderService;
import com.example.bai1.Service.Doctor.OrderItemService;
import com.example.bai1.Service.VnPayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/cart")
public class CartRestController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProductsRepository productsRepository;
    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderItemService orderItemService;
    @Autowired
    private VnPayService vnPayService;
    @Autowired
    private jakarta.servlet.http.HttpServletRequest request;

    @PostMapping("/checkout")
    public ResponseEntity<?> checkout(@RequestBody CartRequest req) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal().equals("anonymousUser")) {
            return ResponseEntity.status(401).body("Vui lòng đăng nhập để thanh toán!");
        }
        String username = auth.getName();
        User user = userRepository.findByAccountUsername(username);
        if (user == null) user = userRepository.findByAccountEmail(username);
        if (user == null) return ResponseEntity.status(403).body("Không tìm thấy thông tin người dùng!");
        if (req.items == null || req.items.isEmpty()) return ResponseEntity.badRequest().body("Giỏ hàng trống!");
        List<Order_items> orderItems = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;
        Map<Long, Products> productCache = new HashMap<>();
        for (CartItem item : req.items) {
            Products product = productCache.computeIfAbsent(item.productId, id -> productsRepository.findById(id).orElse(null));
            if (product == null) return ResponseEntity.badRequest().body("Sản phẩm không tồn tại!");
            if (product.getCount() < item.quantity) return ResponseEntity.badRequest().body("Sản phẩm " + product.getName() + " không đủ số lượng!");
            // Trừ kho
            product.setCount(product.getCount() - item.quantity);
            productsRepository.save(product);
            Order_items orderItem = Order_items.builder()
                    .product(product)
                    .quantity(item.quantity)
                    .price(product.getPrice())
                    .build();
            orderItems.add(orderItem);
            total = total.add(product.getPrice().multiply(BigDecimal.valueOf(item.quantity)));
        }
        Orders order = Orders.builder()
                .patient(user)
                .totalAmount(total)
                .orderDate(LocalDateTime.now())
                .status("PAID")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .orderItems(new ArrayList<>())
                .build();
        orderService.save(order);
        // Gán order cho từng orderItem và lưu
        for (Order_items item : orderItems) {
            item.setOrder(order);
            orderItemService.save(item);
            order.getOrderItems().add(item);
        }
        orderService.save(order);
        return ResponseEntity.ok("Thanh toán thành công! Mã đơn hàng: " + order.getOrderId());
    }

    @PostMapping("/checkout-vnpay")
    public ResponseEntity<?> checkoutVnpay(@RequestBody CartRequest req) throws Exception {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal().equals("anonymousUser")) {
            return ResponseEntity.status(401).body("Vui lòng đăng nhập để thanh toán!");
        }
        String username = auth.getName();
        User user = userRepository.findByAccountUsername(username);
        if (user == null) user = userRepository.findByAccountEmail(username);
        if (user == null) return ResponseEntity.status(403).body("Không tìm thấy thông tin người dùng!");
        if (req.items == null || req.items.isEmpty()) return ResponseEntity.badRequest().body("Giỏ hàng trống!");
        List<Order_items> orderItems = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;
        Map<Long, Products> productCache = new HashMap<>();
        for (CartItem item : req.items) {
            Products product = productCache.computeIfAbsent(item.productId, id -> productsRepository.findById(id).orElse(null));
            if (product == null) return ResponseEntity.badRequest().body("Sản phẩm không tồn tại!");
            if (product.getCount() < item.quantity) return ResponseEntity.badRequest().body("Sản phẩm " + product.getName() + " không đủ số lượng!");
            Order_items orderItem = Order_items.builder()
                    .product(product)
                    .quantity(item.quantity)
                    .price(product.getPrice())
                    .build();
            orderItems.add(orderItem);
            total = total.add(product.getPrice().multiply(BigDecimal.valueOf(item.quantity)));
        }
        Orders order = Orders.builder()
                .patient(user)
                .totalAmount(total)
                .orderDate(LocalDateTime.now())
                .status("PENDING")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .orderItems(new ArrayList<>())
                .build();
        orderService.save(order);
        for (Order_items item : orderItems) {
            item.setOrder(order);
            orderItemService.save(item);
            order.getOrderItems().add(item);
        }
        orderService.save(order);
        // Tạo URL QR code VNPAY
        String paymentUrl = vnPayService.createPaymentUrl(request, total.longValue(), "order", String.valueOf(order.getOrderId()));
        return ResponseEntity.ok(Map.of("url", paymentUrl));
    }

    // DTOs
    public static class CartRequest {
        public List<CartItem> items;
    }
    public static class CartItem {
        public Long productId;
        public int quantity;
    }
} 