package com.example.bai1.Controller.Admin;

import com.example.bai1.Service.ReviewService;
import com.example.bai1.Service.Doctor.DoctorService;
import com.example.bai1.Service.OrderMembershipService;
import com.example.bai1.Service.Doctor.MembershipsService;
import com.example.bai1.Model.Reviews;
import com.example.bai1.Model.Reviews.ReviewStatus;
import com.example.bai1.Model.Doctor;
import com.example.bai1.Model.Memberships;
import com.example.bai1.Model.order_membership;
import com.example.bai1.Model.order_membership.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.math.BigDecimal;
import java.util.Arrays;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.YearMonth;
import java.util.Calendar;
import java.util.Comparator;
import java.util.stream.Collectors;
import java.util.Set;
import java.util.HashSet;
import java.util.ArrayList;
import com.example.bai1.Repository.Doctor.AccountRepository;
import com.example.bai1.Model.Account;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import com.example.bai1.Repository.Doctor.DoctorRepository;
import com.example.bai1.Repository.Doctor.MembershipsRepository;
import com.example.bai1.Repository.Doctor.ProductsRepository;
import com.example.bai1.Model.Products;
import com.example.bai1.Repository.OrderMembershipRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.core.io.ByteArrayResource;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.ByteArrayOutputStream;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;
import java.awt.Color;
import com.example.bai1.Model.User;
import com.example.bai1.Repository.Doctor.UserRepository;
import com.example.bai1.Repository.Doctor.AppointmentsRepository;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final ReviewService reviewService;
    private final DoctorService doctorService;
    private final OrderMembershipService orderMembershipService;
    private final MembershipsService membershipsService;
    private final AccountRepository accountRepository;
    private final DoctorRepository doctorRepository;
    private final MembershipsRepository membershipsRepository;
    private final ProductsRepository productsRepository;
    private final OrderMembershipRepository orderMembershipRepository;
    private final UserRepository userRepository;
    private final AppointmentsRepository appointmentsRepository;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    @Autowired
    public AdminController(ReviewService reviewService, DoctorService doctorService,
            OrderMembershipService orderMembershipService, MembershipsService membershipsService,
            AccountRepository accountRepository,
            DoctorRepository doctorRepository,
            MembershipsRepository membershipsRepository,
            ProductsRepository productsRepository,
            OrderMembershipRepository orderMembershipRepository,
            UserRepository userRepository,
            AppointmentsRepository appointmentsRepository) {
        this.reviewService = reviewService;
        this.doctorService = doctorService;
        this.orderMembershipService = orderMembershipService;
        this.membershipsService = membershipsService;
        this.accountRepository = accountRepository;
        this.doctorRepository = doctorRepository;
        this.membershipsRepository = membershipsRepository;
        this.productsRepository = productsRepository;
        this.orderMembershipRepository = orderMembershipRepository;
        this.userRepository = userRepository;
        this.appointmentsRepository = appointmentsRepository;
    }

    @GetMapping({ "", "/", "dashboard" })
    public String adminHome(Model model, HttpServletRequest request) {
        model.addAttribute("currentUri", request.getRequestURI());
        // Tổng số người dùng
        long totalUsers = userRepository.count();
        model.addAttribute("totalUsers", totalUsers);
        // Tổng số tài khoản
        long totalAccounts = accountRepository.count();
        model.addAttribute("totalAccounts", totalAccounts);
        // Số đối tác cần duyệt
        long partnersPending = doctorRepository.findByStatus(com.example.bai1.Model.Doctor.Status.PENDING,
                org.springframework.data.domain.Pageable.unpaged()).getTotalElements();
        model.addAttribute("partnersPending", partnersPending);
        // Số order_membership cần duyệt
        long ordersPending = orderMembershipRepository
                .findByStatus(com.example.bai1.Model.order_membership.Status.PENDING).size();
        model.addAttribute("ordersPending", ordersPending);
        // Tổng doanh thu đã duyệt
        java.math.BigDecimal totalRevenue = orderMembershipRepository
                .findAllByStatus(com.example.bai1.Model.order_membership.Status.APPROVED)
                .stream().map(com.example.bai1.Model.order_membership::getTotalprice)
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
        model.addAttribute("totalRevenue", totalRevenue);
        // Số review chờ duyệt
        long pendingReviews = reviewService.findAllReviews().stream()
                .filter(r -> r.getStatus() == com.example.bai1.Model.Reviews.ReviewStatus.PENDING).count();
        model.addAttribute("pendingReviews", pendingReviews);
        return "admin/dashboard";
    }

    @GetMapping("/accounts")
    public String manageAccounts(Model model, HttpServletRequest request,
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "role", required = false) String role,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortField", defaultValue = "accountId") String sortField,
            @RequestParam(value = "sortDir", defaultValue = "asc") String sortDir) {
        model.addAttribute("currentUri", request.getRequestURI());
        Sort sort = Sort.by(sortField);
        sort = sortDir.equalsIgnoreCase("asc") ? sort.ascending() : sort.descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Account> accountsPage;
        if ((search != null && !search.isEmpty()) && (role != null && !role.isEmpty())) {
            accountsPage = accountRepository.findByUsernameContainingIgnoreCaseAndRole(search,
                    Account.Role.valueOf(role), pageable);
        } else if (search != null && !search.isEmpty()) {
            accountsPage = accountRepository.findByUsernameContainingIgnoreCase(search, pageable);
        } else if (role != null && !role.isEmpty()) {
            accountsPage = accountRepository.findByRole(Account.Role.valueOf(role), pageable);
        } else {
            accountsPage = accountRepository.findAll(pageable);
        }
        model.addAttribute("accountsPage", accountsPage);
        model.addAttribute("accounts", accountsPage.getContent());
        model.addAttribute("search", search);
        model.addAttribute("role", role);
        model.addAttribute("roles", Account.Role.values());
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        return "admin/manage-accounts";
    }

    @GetMapping("/accounts/new")
    public String showAddAccountForm(Model model, HttpServletRequest request) {
        model.addAttribute("currentUri", request.getRequestURI());
        model.addAttribute("account", new Account());
        model.addAttribute("roles", Account.Role.values());
        model.addAttribute("providers", Account.AuthProvider.values());
        return "admin/account-form";
    }

    @PostMapping("/accounts/new")
    public String addAccount(Account account, RedirectAttributes redirectAttributes) {
        try {
            if (accountRepository.findByUsername(account.getUsername()) != null
                    || accountRepository.findByEmail(account.getEmail()) != null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Username hoặc Email đã tồn tại!");
                return "redirect:/admin/accounts/new";
            }
            account.setCreatedAt(java.time.LocalDateTime.now());
            account.setUpdatedAt(java.time.LocalDateTime.now());
            account.setRole(Account.Role.PATIENT);
            // TODO: mã hoá password nếu cần
            accountRepository.save(account);
            // Tạo user mới liên kết với account này
            User user = new User();
            user.setAccount(account);
            user.setFullName(account.getUsername());
            user.setPhoneNumber("");
            user.setAddress("");
            user.setNote("");
            user.setFollowup(0);
            user.setCreatedAt(java.time.LocalDateTime.now());
            user.setUpdatedAt(java.time.LocalDateTime.now());
            userRepository.save(user);
            redirectAttributes.addFlashAttribute("successMessage", "Thêm tài khoản và người dùng thành công!");
            return "redirect:/admin/accounts";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi thêm tài khoản: " + e.getMessage());
            return "redirect:/admin/accounts/new";
        }
    }

    @GetMapping("/accounts/edit/{id}")
    public String showEditAccountForm(@PathVariable Long id, Model model, HttpServletRequest request,
            RedirectAttributes redirectAttributes) {
        model.addAttribute("currentUri", request.getRequestURI());
        Account account = accountRepository.findById(id).orElse(null);
        if (account == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy tài khoản!");
            return "redirect:/admin/accounts";
        }
        model.addAttribute("account", account);
        model.addAttribute("roles", Account.Role.values());
        model.addAttribute("providers", Account.AuthProvider.values());
        return "admin/account-form";
    }

    @PostMapping("/accounts/edit/{id}")
    public String editAccount(@PathVariable Long id, Account formAccount, RedirectAttributes redirectAttributes) {
        try {
            Account account = accountRepository.findById(id).orElse(null);
            if (account == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy tài khoản!");
                return "redirect:/admin/accounts";
            }
            account.setUsername(formAccount.getUsername());
            account.setEmail(formAccount.getEmail());
            if (formAccount.getPassword() != null && !formAccount.getPassword().isEmpty()) {
                account.setPassword(formAccount.getPassword()); // TODO: mã hoá nếu cần
            }
            account.setRole(formAccount.getRole());
            account.setProvider(formAccount.getProvider());
            account.setUpdatedAt(java.time.LocalDateTime.now());
            accountRepository.save(account);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật tài khoản thành công!");
            return "redirect:/admin/accounts";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi cập nhật tài khoản: " + e.getMessage());
            return "redirect:/admin/accounts/edit/" + id;
        }
    }

    @PostMapping("/accounts/delete/{id}")
    public String deleteAccount(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            accountRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Xoá tài khoản thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi xoá tài khoản: " + e.getMessage());
        }
        return "redirect:/admin/accounts";
    }

    @GetMapping("/partners")
    public String managePartners(Model model, HttpServletRequest request,
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortField", defaultValue = "doctorId") String sortField,
            @RequestParam(value = "sortDir", defaultValue = "asc") String sortDir,
            // order_membership tab params
            @RequestParam(value = "tab", required = false) String tab,
            @RequestParam(value = "searchOrder", required = false) String searchOrder,
            @RequestParam(value = "statusOrder", required = false) String statusOrder,
            @RequestParam(value = "membershipId", required = false) Long membershipId,
            @RequestParam(value = "pageOrder", defaultValue = "0") int pageOrder,
            @RequestParam(value = "sizeOrder", defaultValue = "10") int sizeOrder,
            @RequestParam(value = "sortFieldOrder", defaultValue = "id") String sortFieldOrder,
            @RequestParam(value = "sortDirOrder", defaultValue = "desc") String sortDirOrder,
            @RequestParam(value = "typeOrder", required = false) String typeOrder) {
        model.addAttribute("currentUri", request.getRequestURI());
        // Tab doctor (default)
        org.springframework.data.domain.Sort sort = org.springframework.data.domain.Sort.by(sortField);
        sort = sortDir.equalsIgnoreCase("asc") ? sort.ascending() : sort.descending();
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size,
                sort);
        org.springframework.data.domain.Page<Doctor> doctorsPage;
        if ((search != null && !search.isEmpty()) && (status != null && !status.isEmpty())) {
            doctorsPage = doctorRepository.findByFullnameContainingIgnoreCaseAndStatus(search,
                    Doctor.Status.valueOf(status), pageable);
        } else if (search != null && !search.isEmpty()) {
            doctorsPage = doctorRepository.findByFullnameContainingIgnoreCase(search, pageable);
        } else if (status != null && !status.isEmpty()) {
            doctorsPage = doctorRepository.findByStatus(Doctor.Status.valueOf(status), pageable);
        } else {
            doctorsPage = doctorRepository.findAll(pageable);
        }
        model.addAttribute("doctorsPage", doctorsPage);
        model.addAttribute("doctors", doctorsPage.getContent());
        model.addAttribute("search", search);
        model.addAttribute("status", status);
        model.addAttribute("statuses", Doctor.Status.values());
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("size", size);
        // Tab order_membership
        if ("orders".equals(tab)) {
            org.springframework.data.domain.Sort sortOrder = org.springframework.data.domain.Sort.by(sortFieldOrder);
            sortOrder = sortDirOrder.equalsIgnoreCase("asc") ? sortOrder.ascending() : sortOrder.descending();
            org.springframework.data.domain.Pageable pageableOrder = org.springframework.data.domain.PageRequest
                    .of(pageOrder, sizeOrder, sortOrder);
            org.springframework.data.domain.Page<order_membership> ordersPage;
            // Lọc theo typeOrder nếu có
            if (typeOrder != null && !typeOrder.isEmpty()) {
                order_membership.OrderType typeEnum = order_membership.OrderType.valueOf(typeOrder);
                if ((searchOrder != null && !searchOrder.isEmpty()) && (statusOrder != null && !statusOrder.isEmpty())
                        && membershipId != null) {
                    ordersPage = orderMembershipRepository
                            .findByDoctor_FullnameContainingIgnoreCaseAndStatusAndMembership_MembershipIdAndType(
                                    searchOrder, Status.valueOf(statusOrder), membershipId, typeEnum, pageableOrder);
                } else if ((searchOrder != null && !searchOrder.isEmpty())
                        && (statusOrder != null && !statusOrder.isEmpty())) {
                    ordersPage = orderMembershipRepository.findByDoctor_FullnameContainingIgnoreCaseAndStatusAndType(
                            searchOrder, Status.valueOf(statusOrder), typeEnum, pageableOrder);
                } else if ((searchOrder != null && !searchOrder.isEmpty()) && membershipId != null) {
                    ordersPage = orderMembershipRepository
                            .findByDoctor_FullnameContainingIgnoreCaseAndMembership_MembershipIdAndType(searchOrder,
                                    membershipId, typeEnum, pageableOrder);
                } else if (statusOrder != null && !statusOrder.isEmpty() && membershipId != null) {
                    ordersPage = orderMembershipRepository.findByStatusAndMembership_MembershipIdAndType(
                            Status.valueOf(statusOrder), membershipId, typeEnum, pageableOrder);
                } else if (searchOrder != null && !searchOrder.isEmpty()) {
                    ordersPage = orderMembershipRepository.findByDoctor_FullnameContainingIgnoreCaseAndType(searchOrder,
                            typeEnum, pageableOrder);
                } else if (statusOrder != null && !statusOrder.isEmpty()) {
                    ordersPage = orderMembershipRepository.findByStatusAndType(Status.valueOf(statusOrder), typeEnum,
                            pageableOrder);
                } else if (membershipId != null) {
                    ordersPage = orderMembershipRepository.findByMembership_MembershipIdAndType(membershipId, typeEnum,
                            pageableOrder);
                } else {
                    ordersPage = orderMembershipRepository.findByType(typeEnum, pageableOrder);
                }
            } else {
                // Không filter theo type
                if ((searchOrder != null && !searchOrder.isEmpty()) && (statusOrder != null && !statusOrder.isEmpty())
                        && membershipId != null) {
                    ordersPage = orderMembershipRepository
                            .findByDoctor_FullnameContainingIgnoreCaseAndStatusAndMembership_MembershipId(searchOrder,
                                    Status.valueOf(statusOrder), membershipId, pageableOrder);
                } else if ((searchOrder != null && !searchOrder.isEmpty())
                        && (statusOrder != null && !statusOrder.isEmpty())) {
                    ordersPage = orderMembershipRepository.findByDoctor_FullnameContainingIgnoreCaseAndStatus(
                            searchOrder, Status.valueOf(statusOrder), pageableOrder);
                } else if ((searchOrder != null && !searchOrder.isEmpty()) && membershipId != null) {
                    ordersPage = orderMembershipRepository
                            .findByDoctor_FullnameContainingIgnoreCaseAndMembership_MembershipId(searchOrder,
                                    membershipId, pageableOrder);
                } else if (statusOrder != null && !statusOrder.isEmpty() && membershipId != null) {
                    ordersPage = orderMembershipRepository.findByStatusAndMembership_MembershipId(
                            Status.valueOf(statusOrder), membershipId, pageableOrder);
                } else if (searchOrder != null && !searchOrder.isEmpty()) {
                    ordersPage = orderMembershipRepository.findByDoctor_FullnameContainingIgnoreCase(searchOrder,
                            pageableOrder);
                } else if (statusOrder != null && !statusOrder.isEmpty()) {
                    ordersPage = orderMembershipRepository.findByStatus(Status.valueOf(statusOrder), pageableOrder);
                } else if (membershipId != null) {
                    ordersPage = orderMembershipRepository.findByMembership_MembershipId(membershipId, pageableOrder);
                } else {
                    ordersPage = orderMembershipRepository.findAll(pageableOrder);
                }
            }
            model.addAttribute("ordersPage", ordersPage);
            model.addAttribute("orders", ordersPage.getContent());
            model.addAttribute("searchOrder", searchOrder);
            model.addAttribute("statusOrder", statusOrder);
            model.addAttribute("membershipId", membershipId);
            model.addAttribute("sortFieldOrder", sortFieldOrder);
            model.addAttribute("sortDirOrder", sortDirOrder);
            model.addAttribute("sizeOrder", sizeOrder);
            model.addAttribute("statusesOrder", Status.values());
            model.addAttribute("allMemberships", membershipsRepository.findAll());
            model.addAttribute("typeOrder", typeOrder);
            model.addAttribute("tab", "orders");
        }
        return "admin/manage-partners";
    }

    @GetMapping("/revenue-stats")
    public String revenueStatistics(Model model, HttpServletRequest request,
            @RequestParam(required = false) Long filterMembershipId,
            @RequestParam(required = false) Integer selectedYearParam,
            @RequestParam(required = false) Integer selectedMonthParam,
            @RequestParam(value = "orderTypeParam", required = false) String orderTypeParam) {
        model.addAttribute("currentUri", request.getRequestURI());

        YearMonth currentYearMonth = YearMonth.now();
        int yearToDisplay = (selectedYearParam == null) ? currentYearMonth.getYear() : selectedYearParam;
        int monthToDisplay = (selectedMonthParam == null) ? currentYearMonth.getMonthValue() : selectedMonthParam;

        // Fetch distinct year-months from DB
        List<String> distinctYearMonthsRaw = orderMembershipService.getDistinctYearMonths();
        Set<YearMonth> yearMonthSet = new HashSet<>();
        yearMonthSet.add(currentYearMonth); // Ensure current year/month is available

        for (String ymStr : distinctYearMonthsRaw) {
            try {
                yearMonthSet.add(YearMonth.parse(ymStr, DateTimeFormatter.ofPattern("yyyy-MM")));
            } catch (java.time.format.DateTimeParseException e) {
                System.err.println("Error parsing year-month string: " + ymStr + "; " + e.getMessage());
            }
        }

        List<YearMonth> allAvailableYearMonths = new ArrayList<>(yearMonthSet);
        allAvailableYearMonths.sort(Comparator.reverseOrder()); // Newest first

        List<Integer> filterYears = allAvailableYearMonths.stream()
                .map(YearMonth::getYear)
                .distinct()
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toList());

        model.addAttribute("filterYears", filterYears);
        model.addAttribute("selectedFilterYear", yearToDisplay);
        model.addAttribute("selectedFilterMonth", monthToDisplay);
        model.addAttribute("orderTypeParam", orderTypeParam);

        // Fetch order memberships for the table (uses yearToDisplay, monthToDisplay,
        // filterMembershipId, orderTypeParam)
        List<order_membership> orderMembershipsForTable = orderMembershipService
                .getApprovedOrdersByYearAndMonthAndType(yearToDisplay, monthToDisplay, filterMembershipId,
                        orderTypeParam);
        model.addAttribute("orderMemberships", orderMembershipsForTable);

        // Fetch data for the new stacked bar chart (monthly revenue by membership type
        // for yearToDisplay, filter by type if needed)
        Map<String, Object> monthlyStackedRevenueChartData = orderMembershipService
                .getMonthlyRevenueByMembershipTypeChartData(yearToDisplay, orderTypeParam);
        model.addAttribute("monthlyStackedRevenueChartData", monthlyStackedRevenueChartData);

        List<Memberships> allMemberships = membershipsService.getAllMemberships();
        model.addAttribute("allMemberships", allMemberships);
        model.addAttribute("selectedMembershipId", filterMembershipId);

        // Tổng doanh thu đã duyệt (có thể filter theo type)
        BigDecimal totalRevenue = orderMembershipService.getTotalApprovedRevenue(filterMembershipId, orderTypeParam);
        model.addAttribute("totalMembershipRevenue", totalRevenue);

        // Doanh thu theo bác sĩ (có thể filter theo type)
        List<Map<String, Object>> revenueByDoctor = orderMembershipService
                .getRevenueByDoctorAndMembership(filterMembershipId, orderTypeParam);
        model.addAttribute("revenueByDoctor", revenueByDoctor);

        return "admin/revenue-stats";
    }

    @GetMapping("/revenue-stats/export-excel")
    public ResponseEntity<ByteArrayResource> exportRevenueStatsExcel(
            @RequestParam(required = false) Long filterMembershipId,
            @RequestParam(required = false) Integer selectedYearParam,
            @RequestParam(required = false) Integer selectedMonthParam,
            @RequestParam(value = "orderTypeParam", required = false) String orderTypeParam) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Revenue Stats");
            int rowIdx = 0;
            org.apache.poi.ss.usermodel.Row filterRow = sheet.createRow(rowIdx++);
            filterRow.createCell(0).setCellValue("Bộ lọc:");
            filterRow.createCell(1).setCellValue("Năm: " + (selectedYearParam != null ? selectedYearParam : "Tất cả"));
            filterRow.createCell(2)
                    .setCellValue("Tháng: " + (selectedMonthParam != null ? selectedMonthParam : "Tất cả"));
            filterRow.createCell(3)
                    .setCellValue("Membership: " + (filterMembershipId != null ? filterMembershipId : "Tất cả"));
            filterRow.createCell(4).setCellValue("Loại order: " + (orderTypeParam != null ? orderTypeParam : "Tất cả"));
            rowIdx++;
            BigDecimal totalRevenue = orderMembershipService.getTotalApprovedRevenue(filterMembershipId,
                    orderTypeParam);
            List<order_membership> orders = orderMembershipService.getApprovedOrdersByYearAndMonthAndType(
                    selectedYearParam, selectedMonthParam, filterMembershipId, orderTypeParam);
            org.apache.poi.ss.usermodel.Row sumRow = sheet.createRow(rowIdx++);
            sumRow.createCell(0).setCellValue("Tổng doanh thu:");
            sumRow.createCell(1).setCellValue(totalRevenue != null ? totalRevenue.doubleValue() : 0.0);
            sumRow.createCell(2).setCellValue("Tổng số đơn:");
            sumRow.createCell(3).setCellValue(orders.size());
            rowIdx++;
            org.apache.poi.ss.usermodel.Row header = sheet.createRow(rowIdx++);
            String[] headers = { "ID", "Tên Bác Sĩ", "Tên Membership", "Số Lượng", "Tổng Giá", "Loại order",
                    "Trạng Thái", "Ngày Tạo" };
            for (int i = 0; i < headers.length; i++)
                header.createCell(i).setCellValue(headers[i]);
            for (order_membership order : orders) {
                org.apache.poi.ss.usermodel.Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(order.getId());
                row.createCell(1).setCellValue(order.getDoctor().getFullname());
                row.createCell(2).setCellValue(order.getMembership().getName());
                row.createCell(3).setCellValue(order.getCount() != null ? order.getCount() : 0);
                row.createCell(4)
                        .setCellValue(order.getTotalprice() != null ? order.getTotalprice().doubleValue() : 0.0);
                String typeStr = order.getType() != null
                        ? (order.getType() == order_membership.OrderType.RENEWAL ? "Gia hạn"
                                : (order.getType() == order_membership.OrderType.UPGRADE ? "Nâng cấp" : "Khác"))
                        : "Khác";
                row.createCell(5).setCellValue(typeStr);
                row.createCell(6).setCellValue(order.getStatus() != null ? order.getStatus().name() : "");
                row.createCell(7).setCellValue(order.getCreatedAt() != null ? order.getCreatedAt().toString() : "");
            }
            for (int i = 0; i < headers.length; i++)
                sheet.autoSizeColumn(i);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            workbook.write(bos);
            ByteArrayResource resource = new ByteArrayResource(bos.toByteArray());
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=revenue-stats.xlsx")
                    .contentType(MediaType
                            .parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .contentLength(resource.contentLength())
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/revenue-stats/export-pdf")
    public ResponseEntity<ByteArrayResource> exportRevenueStatsPdf(
            @RequestParam(required = false) Long filterMembershipId,
            @RequestParam(required = false) Integer selectedYearParam,
            @RequestParam(required = false) Integer selectedMonthParam,
            @RequestParam(value = "orderTypeParam", required = false) String orderTypeParam) {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            com.lowagie.text.Document document = new com.lowagie.text.Document(com.lowagie.text.PageSize.A4.rotate());
            com.lowagie.text.pdf.PdfWriter.getInstance(document, bos);
            document.open();
            com.lowagie.text.Font titleFont = new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 18,
                    com.lowagie.text.Font.BOLD);
            com.lowagie.text.Paragraph title = new com.lowagie.text.Paragraph("Thống kê doanh thu Membership",
                    titleFont);
            title.setAlignment(com.lowagie.text.Element.ALIGN_CENTER);
            document.add(title);
            document.add(new com.lowagie.text.Paragraph(" "));
            com.lowagie.text.Font filterFont = new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 12,
                    com.lowagie.text.Font.NORMAL);
            document.add(new com.lowagie.text.Paragraph("Bộ lọc:", filterFont));
            document.add(new com.lowagie.text.Paragraph(
                    "Năm: " + (selectedYearParam != null ? selectedYearParam : "Tất cả"), filterFont));
            document.add(new com.lowagie.text.Paragraph(
                    "Tháng: " + (selectedMonthParam != null ? selectedMonthParam : "Tất cả"), filterFont));
            document.add(new com.lowagie.text.Paragraph(
                    "Membership: " + (filterMembershipId != null ? filterMembershipId : "Tất cả"), filterFont));
            document.add(new com.lowagie.text.Paragraph(
                    "Loại order: " + (orderTypeParam != null ? orderTypeParam : "Tất cả"), filterFont));
            document.add(new com.lowagie.text.Paragraph(" "));
            BigDecimal totalRevenue = orderMembershipService.getTotalApprovedRevenue(filterMembershipId,
                    orderTypeParam);
            List<order_membership> orders = orderMembershipService.getApprovedOrdersByYearAndMonthAndType(
                    selectedYearParam, selectedMonthParam, filterMembershipId, orderTypeParam);
            document.add(new com.lowagie.text.Paragraph("Tổng doanh thu: " + (totalRevenue != null ? totalRevenue : 0),
                    filterFont));
            document.add(new com.lowagie.text.Paragraph("Tổng số đơn: " + orders.size(), filterFont));
            document.add(new com.lowagie.text.Paragraph(" "));
            com.lowagie.text.pdf.PdfPTable table = new com.lowagie.text.pdf.PdfPTable(8);
            table.setWidthPercentage(100);
            String[] headers = { "ID", "Tên Bác Sĩ", "Tên Membership", "Số Lượng", "Tổng Giá", "Loại order",
                    "Trạng Thái", "Ngày Tạo" };
            for (String h : headers) {
                com.lowagie.text.pdf.PdfPCell cell = new com.lowagie.text.pdf.PdfPCell(new com.lowagie.text.Phrase(h,
                        new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 12, com.lowagie.text.Font.BOLD)));
                cell.setBackgroundColor(new java.awt.Color(220, 220, 220));
                table.addCell(cell);
            }
            for (order_membership order : orders) {
                table.addCell(order.getId() != null ? order.getId().toString() : "");
                table.addCell(order.getDoctor() != null ? order.getDoctor().getFullname() : "");
                table.addCell(order.getMembership() != null ? order.getMembership().getName() : "");
                table.addCell(order.getCount() != null ? order.getCount().toString() : "0");
                table.addCell(order.getTotalprice() != null ? order.getTotalprice().toString() : "0");
                String typeStr = order.getType() != null
                        ? (order.getType() == order_membership.OrderType.RENEWAL ? "Gia hạn"
                                : (order.getType() == order_membership.OrderType.UPGRADE ? "Nâng cấp" : "Khác"))
                        : "Khác";
                table.addCell(typeStr);
                table.addCell(order.getStatus() != null ? order.getStatus().name() : "");
                table.addCell(order.getCreatedAt() != null ? order.getCreatedAt().toString() : "");
            }
            document.add(table);
            document.close();
            ByteArrayResource resource = new ByteArrayResource(bos.toByteArray());
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=revenue-stats.pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .contentLength(resource.contentLength())
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/reviews")
    public String manageReviews(Model model, HttpServletRequest request,
            @RequestParam(required = false) Long filterDoctorId,
            @RequestParam(required = false) Integer filterRating,
            @RequestParam(required = false) ReviewStatus filterStatus) {
        model.addAttribute("currentUri", request.getRequestURI());

        List<Doctor> doctors = doctorService.getAllDoctors();
        model.addAttribute("doctors", doctors);

        List<Reviews> reviewsList = reviewService.findReviewsByFilters(filterDoctorId, filterRating, filterStatus);
        model.addAttribute("reviewsList", reviewsList);

        model.addAttribute("reviewStatuses", ReviewStatus.values());

        // To retain filter values in the form after submission
        model.addAttribute("selectedDoctorId", filterDoctorId);
        model.addAttribute("selectedRating", filterRating);
        model.addAttribute("selectedStatus", filterStatus);

        return "admin/manage-reviews";
    }

    @PostMapping("/reviews/approve/{id}")
    public String approveReview(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            reviewService.approveReview(id);
            redirectAttributes.addFlashAttribute("successMessage", "Review approved successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error approving review: " + e.getMessage());
        }
        return "redirect:/admin/reviews";
    }

    @PostMapping("/reviews/reject/{id}")
    public String rejectReview(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            reviewService.rejectReview(id);
            redirectAttributes.addFlashAttribute("successMessage", "Review rejected successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error rejecting review: " + e.getMessage());
        }
        return "redirect:/admin/reviews";
    }

    @PostMapping("/reviews/delete/{id}")
    public String deleteReview(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            reviewService.deleteReview(id);
            redirectAttributes.addFlashAttribute("successMessage", "Review deleted successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting review: " + e.getMessage());
        }
        return "redirect:/admin/reviews";
    }

    @PostMapping("/partners/approve/{id}")
    public String approveDoctor(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Doctor doctor = doctorRepository.findById(id).orElse(null);
            if (doctor == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy bác sĩ!");
                return "redirect:/admin/partners";
            }
            doctor.setStatus(Doctor.Status.APPROVED);
            // Đổi role account thành DOCTOR
            Account account = doctor.getAccount();
            if (account != null) {
                account.setRole(Account.Role.DOCTOR);
                accountRepository.save(account);
            }
            // Set membership là basic, startdate hôm nay, enddate sau 1 tháng
            Memberships basicMembership = membershipsRepository.findAll().stream()
                    .filter(m -> m.getName().equalsIgnoreCase("basic")).findFirst().orElse(null);
            if (basicMembership != null) {
                doctor.setMembership(basicMembership);
                doctor.setStartdate(LocalDate.now());
                doctor.setEnddate(LocalDate.now().plusMonths(1));
            }
            doctorRepository.save(doctor);
            redirectAttributes.addFlashAttribute("successMessage", "Duyệt bác sĩ thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi duyệt bác sĩ: " + e.getMessage());
        }
        return "redirect:/admin/partners";
    }

    @PostMapping("/partners/reject/{id}")
    public String rejectDoctor(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Doctor doctor = doctorRepository.findById(id).orElse(null);
            if (doctor == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy bác sĩ!");
                return "redirect:/admin/partners";
            }
            doctor.setStatus(Doctor.Status.REJECTED);
            doctorRepository.save(doctor);
            redirectAttributes.addFlashAttribute("successMessage", "Từ chối bác sĩ thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi từ chối bác sĩ: " + e.getMessage());
        }
        return "redirect:/admin/partners";
    }

    @PostMapping("/partners/delete/{id}")
    public String deleteDoctor(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Doctor doctor = doctorRepository.findById(id).orElse(null);
            if (doctor == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy bác sĩ!");
                return "redirect:/admin/partners";
            }
            // Xoá tất cả products của doctor này
            java.util.List<Products> products = productsRepository.findByDoctor_DoctorId(id);
            if (products != null && !products.isEmpty()) {
                productsRepository.deleteAll(products);
            }
            Account account = doctor.getAccount();
            doctorRepository.deleteById(id);
            if (account != null) {
                accountRepository.deleteById(account.getAccountId());
            }
            redirectAttributes.addFlashAttribute("successMessage",
                    "Xoá bác sĩ, tài khoản liên kết và tất cả sản phẩm thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi xoá bác sĩ: " + e.getMessage());
        }
        return "redirect:/admin/partners";
    }

    @GetMapping("/partners/view/{id}")
    public String viewDoctor(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes,
            HttpServletRequest request) {
        Doctor doctor = doctorRepository.findById(id).orElse(null);
        if (doctor == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy bác sĩ!");
            return "redirect:/admin/partners";
        }
        model.addAttribute("doctor", doctor);
        model.addAttribute("currentUri", request.getRequestURI());
        return "admin/doctor-detail";
    }

    @PostMapping("/partners/orders/approve/{id}")
    public String approveOrderMembership(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            order_membership order = orderMembershipRepository.findById(id).orElse(null);
            if (order == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy đăng ký membership!");
                return "redirect:/admin/partners?tab=orders";
            }
            Doctor doctor = order.getDoctor();
            if (order.getType() == order_membership.OrderType.RENEWAL) {
                // Gia hạn: cộng thêm vào enddate số tháng = order.count
                java.time.LocalDate now = java.time.LocalDate.now();
                java.time.LocalDate currentEnd = doctor.getEnddate();
                if (currentEnd == null || currentEnd.isBefore(now)) {
                    currentEnd = now;
                }
                doctor.setEnddate(currentEnd.plusMonths(order.getCount() != null ? order.getCount() : 1));
            } else if (order.getType() == order_membership.OrderType.UPGRADE) {
                // Nâng cấp: đổi membership, set startdate/enddate mới
                doctor.setMembership(order.getMembership());
                java.time.LocalDate now = java.time.LocalDate.now();
                doctor.setStartdate(now);
                doctor.setEnddate(now.plusMonths(order.getCount() != null ? order.getCount() : 1));
            }
            doctorRepository.save(doctor);
            order.setStatus(order_membership.Status.APPROVED);
            orderMembershipRepository.save(order);
            redirectAttributes.addFlashAttribute("successMessage", "Duyệt đăng ký membership thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi duyệt đăng ký: " + e.getMessage());
        }
        return "redirect:/admin/partners?tab=orders";
    }

    @PostMapping("/partners/orders/reject/{id}")
    public String rejectOrderMembership(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            order_membership order = orderMembershipRepository.findById(id).orElse(null);
            if (order == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy đăng ký membership!");
                return "redirect:/admin/partners?tab=orders";
            }
            order.setStatus(order_membership.Status.REJECTED);
            orderMembershipRepository.save(order);
            redirectAttributes.addFlashAttribute("successMessage", "Từ chối đăng ký membership thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi từ chối đăng ký: " + e.getMessage());
        }
        return "redirect:/admin/partners?tab=orders";
    }

    @GetMapping("/partners/orders/view/{id}")
    public String viewOrderMembership(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes,
            HttpServletRequest request) {
        order_membership order = orderMembershipRepository.findById(id).orElse(null);
        if (order == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy đăng ký membership!");
            return "redirect:/admin/partners?tab=orders";
        }
        model.addAttribute("order", order);
        model.addAttribute("currentUri", request.getRequestURI());
        return "admin/order-membership-detail";
    }

    @GetMapping("/users")
    public String manageUsers(Model model, HttpServletRequest request,
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "sortField", defaultValue = "userId") String sortField,
            @RequestParam(value = "sortDir", defaultValue = "asc") String sortDir,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        model.addAttribute("currentUri", request.getRequestURI());
        Sort sort = Sort.by(sortField);
        sort = sortDir.equalsIgnoreCase("asc") ? sort.ascending() : sort.descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<User> usersPage;
        if (search != null && !search.isEmpty()) {
            usersPage = userRepository.findByFullNameContainingIgnoreCaseOrPhoneNumberContainingIgnoreCase(search,
                    search, pageable);
        } else {
            usersPage = userRepository.findAll(pageable);
        }
        model.addAttribute("usersPage", usersPage);
        model.addAttribute("users", usersPage.getContent());
        model.addAttribute("search", search);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("size", size);
        return "admin/manage-users";
    }

    @GetMapping("/users/new")
    public String showAddUserForm(Model model, HttpServletRequest request) {
        model.addAttribute("currentUri", request.getRequestURI());
        model.addAttribute("user", new User());
        return "admin/user-form";
    }

    @PostMapping("/users/new")
    public String addUser(User user, RedirectAttributes redirectAttributes) {
        try {
            // Tạo account mới cho user này
            Account account = new Account();
            account.setUsername(user.getFullName());
            account.setEmail(user.getPhoneNumber() + "@example.com");
            account.setPassword("123456"); // TODO: mã hoá nếu cần
            account.setRole(Account.Role.PATIENT);
            account.setProvider(Account.AuthProvider.LOCAL);
            account.setCreatedAt(java.time.LocalDateTime.now());
            account.setUpdatedAt(java.time.LocalDateTime.now());
            accountRepository.save(account);
            user.setAccount(account);
            user.setCreatedAt(java.time.LocalDateTime.now());
            user.setUpdatedAt(java.time.LocalDateTime.now());
            userRepository.save(user);
            redirectAttributes.addFlashAttribute("successMessage", "Thêm người dùng và tài khoản thành công!");
            return "redirect:/admin/users";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi thêm người dùng: " + e.getMessage());
            return "redirect:/admin/users/new";
        }
    }

    @GetMapping("/users/edit/{id}")
    public String showEditUserForm(@PathVariable Long id, Model model, HttpServletRequest request,
            RedirectAttributes redirectAttributes) {
        model.addAttribute("currentUri", request.getRequestURI());
        User user = userRepository.findById(id).orElse(null);
        if (user == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy người dùng!");
            return "redirect:/admin/users";
        }
        model.addAttribute("user", user);
        return "admin/user-form";
    }

    @PostMapping("/users/edit/{id}")
    public String editUser(@PathVariable Long id, User formUser, RedirectAttributes redirectAttributes) {
        try {
            User user = userRepository.findById(id).orElse(null);
            if (user == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy người dùng!");
                return "redirect:/admin/users";
            }
            user.setFullName(formUser.getFullName());
            user.setPhoneNumber(formUser.getPhoneNumber());
            user.setAddress(formUser.getAddress());
            user.setNote(formUser.getNote());
            user.setFollowup(formUser.getFollowup());
            user.setUpdatedAt(java.time.LocalDateTime.now());
            userRepository.save(user);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật người dùng thành công!");
            return "redirect:/admin/users";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi cập nhật người dùng: " + e.getMessage());
            return "redirect:/admin/users/edit/" + id;
        }
    }

    @PostMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            userRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Xoá người dùng thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi xoá người dùng: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }

    @GetMapping("/users/view/{id}")
    public String viewUser(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes,
            HttpServletRequest request) {
        User user = userRepository.findById(id).orElse(null);
        if (user == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy người dùng!");
            return "redirect:/admin/users";
        }
        model.addAttribute("user", user);
        model.addAttribute("currentUri", request.getRequestURI());
        return "admin/user-detail";
    }

}
