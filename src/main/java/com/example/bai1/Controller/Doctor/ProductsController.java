package com.example.bai1.Controller.Doctor;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.bai1.Model.Account;
import com.example.bai1.Model.ApiResponse;
import com.example.bai1.Model.CustomUserDetail;
import com.example.bai1.Model.Doctor;
import com.example.bai1.Model.Products;
import com.example.bai1.Repository.Doctor.DoctorRepository;
import com.example.bai1.Repository.Doctor.ProductsRepository;
import com.example.bai1.Service.Doctor.ProductsService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductsController {
    @Autowired
    private final ProductsService productsService;
    @Autowired
    private final DoctorRepository doctorRepository;

    @GetMapping("/getallproduct")
    public ResponseEntity<Map<String, Object>> getAllProducts(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "2") int pagesize,
            @RequestParam(defaultValue = "") String keyword) {
        try {
            // Lấy thông tin người dùng hiện tại
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            CustomUserDetail userDetail = (CustomUserDetail) auth.getPrincipal();
            Account account = userDetail.getAccount();
            Doctor doctor = doctorRepository.findByAccount(account);

            // Lấy kết quả phân trang từ repository
            Page<Products> productPage = productsService.getProductByDoctorAndSearch(doctor.getDoctorId(), keyword,
                    page - 1,
                    pagesize);

            // Tạo response map
            Map<String, Object> response = new HashMap<>();
            response.put("Data", productPage.getContent()); // Dữ liệu của trang hiện tại
            response.put("TotalItems", productPage.getTotalElements()); // Tổng số item
            response.put("CurrentPage", productPage.getNumber() + 1); // Trang hiện tại
            response.put("NumberPage", productPage.getTotalPages()); // Tổng số trang
            response.put("PageSize", pagesize); // Kích thước trang

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("Error", e.getMessage()));
        }
    }

    @PutMapping("/updatecount/{productId}")
    public ResponseEntity<?> updateProductCount(@PathVariable Long productId,
            @RequestBody Map<String, Integer> request) {
        Products productOptional = productsService.getProducts(productId);
        if (productOptional != null) {
            Products product = productOptional;
            product.setCount((long) request.get("count")); // Cập nhật count
            productsService.saveProduct(product);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Cập nhật thành công");
            return ResponseEntity.ok(response);
        } else {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Sản phẩm không tìm thấy");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @PostMapping("/saveProduct")
    public ResponseEntity<?> saveProduct(@ModelAttribute("product") Products product,
            @RequestParam("productimage") MultipartFile file) {
        // xử lý lưu sản phẩm
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetail userDetail = (CustomUserDetail) auth.getPrincipal();
        Account account = userDetail.getAccount();
        Doctor doctor = doctorRepository.findByAccount(account);
        product.setDoctor(doctor);
        if (file != null && !file.isEmpty()) {
            String filename = file.getOriginalFilename();
            String uploadDir = new File("src/main/resources/static/uploads").getAbsolutePath();
            try {
                file.transferTo(new File(uploadDir + File.separator + filename));
                product.setImage("/uploads/" + filename);
            } catch (IllegalStateException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        Products save = productsService.saveProduct(product);
        return ResponseEntity.ok(save);
    }
    // @PostMapping("/savesanpham")
    // public ResponseEntity<?> save(@ModelAttribute("product") Products product,
    // @RequestParam("productimage") MultipartFile file) {
    // try {
    // Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    // CustomUserDetail userDetail = (CustomUserDetail) auth.getPrincipal();
    // Account account = userDetail.getAccount();
    // Doctor doctor = doctorRepository.findByAccount(account);
    // product.setDoctor(doctor);

    // // Xử lý upload file ảnh nếu có
    // if (file != null && !file.isEmpty()) {
    // String filename = file.getOriginalFilename();
    // String uploadDir = new
    // File("src/main/resources/static/uploads").getAbsolutePath();
    // file.transferTo(new File(uploadDir + File.separator + filename));
    // product.setImage("/uploads/" + filename);
    // }

    // // Chỉ xử lý update, không cho phép thêm mới
    // if (product.getProductId() == null || product.getProductId() <= 0) {
    // return ResponseEntity.status(HttpStatus.BAD_REQUEST)
    // .body("Không thể thêm mới, chỉ được phép cập nhật sản phẩm tồn tại.");
    // }

    // // Kiểm tra sản phẩm có tồn tại không
    // Products existingProduct =
    // productsService.getProducts(product.getProductId());
    // if (existingProduct == null) {
    // return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Sản phẩm không tồn
    // tại.");
    // }

    // // Nếu đổi tên, kiểm tra tên có trùng với sản phẩm khác không
    // if (!existingProduct.getName().equals(product.getName())
    // && productsService.existsByNameAndDoctor(product.getName(), doctor)) {
    // return ResponseEntity.status(HttpStatus.CONFLICT).body("Tên sản phẩm đã tồn
    // tại.");
    // }

    // // Cập nhật các trường cần thiết, ví dụ giữ created_at cũ nếu bạn có, hoặc
    // các
    // // trường khác
    // product.setCreatedAt(existingProduct.getCreatedAt()); // nếu có trường này
    // // Các trường khác nếu cần giữ nguyên có thể xử lý tương tự

    // Products saved = productsService.saveProduct(product);
    // return ResponseEntity.ok(saved);

    // } catch (Exception e) {
    // e.printStackTrace();
    // return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi khi
    // lưu sản phẩm.");
    // }
    // }
    // Đặt ngoài class ProductsController để không bị ảnh hưởng bởi
    // @RequestMapping("/api/products")
    @RestController
    public class DoctorProductPublicController {
        @Autowired
        private ProductsService productsService;
        @Autowired
        private DoctorRepository doctorRepository;

        @GetMapping("/api/doctor/{doctorId}/products")
        public ResponseEntity<?> getProductsByDoctorForUser(@PathVariable Long doctorId) {
            Doctor doctor = doctorRepository.findById(doctorId).orElse(null);
            if (doctor == null || doctor.getMembership() == null
                    || !"premium".equalsIgnoreCase(doctor.getMembership().getName())) {
                return ResponseEntity.ok(List.of());
            }
            List<Products> products = productsService.getProductByDoctor(doctorId);
            return ResponseEntity.ok(products);
        }
    }

}
