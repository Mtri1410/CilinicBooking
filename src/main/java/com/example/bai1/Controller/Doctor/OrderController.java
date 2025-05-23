package com.example.bai1.Controller.Doctor;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.bai1.Model.Account;
import com.example.bai1.Model.CustomUserDetail;
import com.example.bai1.Model.Doctor;
import com.example.bai1.Model.Order_items;
import com.example.bai1.Model.Orders;
import com.example.bai1.Model.PatientRevenueDTO;
import com.example.bai1.Model.ProductRevenueDTO;
import com.example.bai1.Model.Products;
import com.example.bai1.Repository.Doctor.DoctorRepository;
import com.example.bai1.Repository.Doctor.OrderRepository;
import com.example.bai1.Repository.Doctor.ProductsRepository;
import com.example.bai1.Service.Doctor.OrderItemService;
import com.example.bai1.Service.Doctor.OrderService;
import com.example.bai1.Service.Doctor.ProductsService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/order")
public class OrderController {
    @Autowired
    private final OrderService orderserService;
    @Autowired
    private final OrderItemService orderItemService;
    @Autowired
    private final ProductsService productsService;
    @Autowired
    private final DoctorRepository doctorRepository;

    @GetMapping("/getallorder")
    public ResponseEntity<List<Orders>> getAllorder() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetail userDetail = (CustomUserDetail) auth.getPrincipal();
        Account account = userDetail.getAccount();
        Doctor doctor = doctorRepository.findByAccount(account);
        List<Products> listproduct = productsService.getProductByDoctor(doctor.getDoctorId());
        List<Order_items> listorderitem = orderItemService.getAllByListProduct(listproduct);
        List<Orders> list = orderserService.getAll(listorderitem);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/getall")
    public ResponseEntity<Map<String, Object>> getAllordertemp(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "2") int pagesize,
            @RequestParam String keyword) {
        try {
            // Lấy thông tin người dùng hiện tại
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            CustomUserDetail userDetail = (CustomUserDetail) auth.getPrincipal();
            Account account = userDetail.getAccount();
            Doctor doctor = doctorRepository.findByAccount(account);
            List<Products> listproduct = productsService.getProductByDoctor(doctor.getDoctorId());
            List<Order_items> listorderitem = orderItemService.getAllByListProduct(listproduct);

            // Lấy kết quả phân trang từ repository
            Page<Orders> orderPage = orderserService.getOrdersByItemsAndSearch(listorderitem, keyword, page
                    - 1, pagesize);

            // Tạo response map
            Map<String, Object> response = new HashMap<>();
            response.put("Data", orderPage.getContent()); // Dữ liệu của trang hiện tại
            response.put("TotalItems", orderPage.getTotalElements()); // Tổng số item
            response.put("CurrentPage", orderPage.getNumber() + 1); // Trang hiện tại
            response.put("NumberPage", orderPage.getTotalPages()); // Tổng số trang
            response.put("PageSize", pagesize); // Kích thước trang

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("Error", e.getMessage()));
        }
    }

    @GetMapping("/order-items")
    public ResponseEntity<Map<String, Object>> getOrderItemsByOrderIdAndProductName(
            @RequestParam Long orderId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "2") int pagesize,
            @RequestParam(required = false) String keyword) {

        Page<Order_items> pageOrderItems = orderItemService.findByOrderIdAndProductName(orderId, keyword, page - 1,
                pagesize);

        Map<String, Object> response = new HashMap<>();
        response.put("Data", pageOrderItems.getContent()); // Dữ liệu của trang hiện tại
        response.put("TotalItems", pageOrderItems.getTotalElements()); // Tổng số item
        response.put("CurrentPage", pageOrderItems.getNumber() + 1); // Trang hiện tại (bắt đầu từ 1)
        response.put("NumberPage", pageOrderItems.getTotalPages()); // Tổng số trang
        response.put("PageSize", pagesize); // Kích thước trang

        return ResponseEntity.ok(response);
    }

    @PostMapping("/updateStatus")
    public ResponseEntity<?> updateOrderStatus(@RequestBody Map<String, Object> payload) {
        try {
            Long orderId = Long.valueOf(payload.get("orderId").toString());
            String status = payload.get("status").toString();

            Orders order = orderserService.getorderbyorderid(orderId);
            if (order == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Order not found");
            }
            if ("CHECK".equalsIgnoreCase(status)) {
                List<Order_items> items = order.getOrderItems();
                for (Order_items item : items) {
                    Products product = item.getProduct();
                    int quantityOrdered = item.getQuantity();
                    long currentStock = product.getCount(); // giả sử có getStockQuantity()
                    long newStock = currentStock - quantityOrdered;
                    if (newStock < 0) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body("Not enough stock for product: " + product.getName());
                    }

                    product.setCount(newStock);
                    productsService.saveProduct(product);
                }
            }
            order.setStatus(status); // Giả sử Orders có setStatus(String)

            orderserService.save(order);

            return ResponseEntity.ok("Status updated");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating status");
        }
    }

    @GetMapping("/revunue/chart")
    public ResponseEntity<?> getRevenueChart(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            CustomUserDetail userDetail = (CustomUserDetail) auth.getPrincipal();
            Account account = userDetail.getAccount();
            Doctor doctor = doctorRepository.findByAccount(account);

            List<PatientRevenueDTO> data = orderserService.getRevenueChart(doctor, startDate, endDate);
            return ResponseEntity.ok(data);
        } catch (Exception ex) {
            ex.printStackTrace(); // Log lỗi chi tiết
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi server: " + ex.getMessage());
        }
    }

    @GetMapping("/revunue/table")
    public ResponseEntity<Map<String, Object>> getRevenueTable(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pagesize) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetail userDetail = (CustomUserDetail) auth.getPrincipal();
        Account account = userDetail.getAccount();
        Doctor doctor = doctorRepository.findByAccount(account);

        Page<PatientRevenueDTO> resultPage = orderserService.getRevenueTable(doctor, startDate, endDate, keyword,
                page - 1,
                pagesize);
        Map<String, Object> response = new HashMap<>();
        response.put("Data", resultPage.getContent());
        response.put("TotalItems", resultPage.getTotalElements());
        response.put("CurrentPage", resultPage.getNumber() + 1); // vì page mặc định bắt đầu từ 0
        response.put("NumberPage", resultPage.getTotalPages());
        response.put("PageSize", pagesize);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/revunueproduct/chart")
    public ResponseEntity<List<ProductRevenueDTO>> getRevenueProductChart(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetail userDetail = (CustomUserDetail) auth.getPrincipal();
        Account account = userDetail.getAccount();
        Doctor doctor = doctorRepository.findByAccount(account);

        List<ProductRevenueDTO> data = orderserService.getRevenueProductChart(doctor, startDate, endDate);
        return ResponseEntity.ok(data);
    }

    @GetMapping("/revunueproduct/table")
    public ResponseEntity<Map<String, Object>> getRevenueProductTable(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pagesize) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetail userDetail = (CustomUserDetail) auth.getPrincipal();
        Account account = userDetail.getAccount();
        Doctor doctor = doctorRepository.findByAccount(account);
        Page<ProductRevenueDTO> revenuePage = orderserService.getRevenueProductTable(doctor, startDate, endDate,
                keyword, page - 1, pagesize);
        Map<String, Object> response = new HashMap<>();
        response.put("Data", revenuePage.getContent());
        response.put("TotalItems", revenuePage.getTotalElements());
        response.put("CurrentPage", revenuePage.getNumber() + 1);
        response.put("NumberPage", revenuePage.getTotalPages());
        response.put("PageSize", pagesize);
        return ResponseEntity.ok(response);
    }
}
