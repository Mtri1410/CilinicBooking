package com.example.bai1.Service;

import com.example.bai1.Model.order_membership;
import com.example.bai1.Model.order_membership.Status;
import com.example.bai1.Repository.OrderMembershipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrderMembershipService {

    private final OrderMembershipRepository orderMembershipRepository;

    @Autowired
    public OrderMembershipService(OrderMembershipRepository orderMembershipRepository) {
        this.orderMembershipRepository = orderMembershipRepository;
    }

    // Lấy danh sách năm-tháng distinct từ order_membership (Fix for error 1 & 5)
    public List<String> getDistinctYearMonths() {
        return orderMembershipRepository.findDistinctYearMonths();
    }

    // Lấy đơn hàng theo năm, tháng, membershipId và loại order
    public List<order_membership> getApprovedOrdersByYearAndMonthAndType(Integer year, Integer month, Long membershipId, String orderTypeParam) {
        order_membership.OrderType type = null;
        if (orderTypeParam != null && !orderTypeParam.isEmpty()) {
            try {
                type = order_membership.OrderType.valueOf(orderTypeParam);
            } catch (Exception ignored) {}
        }
        if (year == null) {
            if (membershipId == null && type == null) {
                return orderMembershipRepository.findAllByStatus(Status.APPROVED);
            } else if (membershipId != null && type == null) {
                return orderMembershipRepository.findAllByMembership_MembershipIdAndStatus(membershipId, Status.APPROVED);
            } else if (membershipId == null && type != null) {
                return orderMembershipRepository.findByType(type, null).getContent();
            } else {
                return orderMembershipRepository.findByMembership_MembershipIdAndType(membershipId, type, null).getContent();
            }
        }
        if (month == null) {
            LocalDateTime start = YearMonth.of(year, 1).atDay(1).atStartOfDay();
            LocalDateTime end = YearMonth.of(year, 12).atEndOfMonth().atTime(23, 59, 59);
            if (membershipId == null && type == null) {
                return orderMembershipRepository.findByStatusAndCreatedAtBetween(Status.APPROVED, start, end);
            } else if (membershipId != null && type == null) {
                return orderMembershipRepository.findByMembership_MembershipIdAndStatusAndCreatedAtBetween(membershipId, Status.APPROVED, start, end);
            } else if (membershipId == null && type != null) {
                // Chưa có hàm repo, cần bổ sung nếu muốn filter theo type + date
                return orderMembershipRepository.findByType(type, null).getContent();
            } else {
                return orderMembershipRepository.findByMembership_MembershipIdAndType(membershipId, type, null).getContent();
            }
        }
        // Có năm, tháng
        if (type == null) {
            return orderMembershipRepository.findByStatusAndYearAndMonthAndOptionalMembership(Status.APPROVED, year, month, membershipId);
        } else {
            // Chưa có hàm repo, cần bổ sung nếu muốn filter theo type + year + month
            // Tạm thời lọc sau khi lấy ra
            final order_membership.OrderType filterType = type;
            return orderMembershipRepository.findByStatusAndYearAndMonthAndOptionalMembership(Status.APPROVED, year, month, membershipId)
                .stream().filter(o -> o.getType() == filterType).collect(Collectors.toList());
        }
    }

    // Tổng doanh thu đã duyệt (có thể filter theo type)
    public BigDecimal getTotalApprovedRevenue(Long membershipId, String orderTypeParam) {
        order_membership.OrderType type = null;
        if (orderTypeParam != null && !orderTypeParam.isEmpty()) {
            try {
                type = order_membership.OrderType.valueOf(orderTypeParam);
            } catch (Exception ignored) {}
        }
        List<order_membership> orders;
        if (type == null) {
            orders = getAllApprovedOrders(membershipId);
        } else {
            if (membershipId != null) {
                orders = orderMembershipRepository.findByMembership_MembershipIdAndType(membershipId, type, null).getContent();
            } else {
                orders = orderMembershipRepository.findByType(type, null).getContent();
            }
        }
        return orders.stream().map(order_membership::getTotalprice).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // Lấy dữ liệu biểu đồ cho năm đã chọn
    public Map<String, Object> getMonthlyRevenueChartData(Integer year, Long membershipId) {
        // Fix for error 7: Corrected call to repository method
        List<Object[]> results = orderMembershipRepository.findMonthlyRevenueByStatusAndMembership(
                Status.APPROVED, membershipId);
        Map<String, BigDecimal> monthlyData = new LinkedHashMap<>();

        int targetYear = year != null ? year : java.time.Year.now().getValue();
        for (int m = 1; m <= 12; m++) {
            String key = String.format("%d-%02d", targetYear, m);
            monthlyData.put(key, BigDecimal.ZERO);
        }

        for (Object[] row : results) {
            Integer rowYear = (Integer) row[0];
            Integer rowMonth = (Integer) row[1];
            // Only include data for the targetYear in this specific chart method
            if (rowYear.equals(targetYear)) {
                String key = String.format("%d-%02d", rowYear, rowMonth);
                monthlyData.put(key, (BigDecimal) row[2]);
            }
        }

        List<String> labels = new ArrayList<>(monthlyData.keySet());
        List<BigDecimal> data = new ArrayList<>(monthlyData.values());

        Map<String, Object> chartData = new HashMap<>();
        chartData.put("labels", labels);
        chartData.put("data", data);
        return chartData;
    }

    // New method for Error 3: Get revenue data for the last N months
    public Map<String, Object> getRevenueForLastNMonthsChartData(int endYear, int endMonth, int numberOfMonths,
            Long membershipId) {
        List<Object[]> allMonthlyResults = orderMembershipRepository.findMonthlyRevenueByStatusAndMembership(
                Status.APPROVED, membershipId);

        Map<YearMonth, BigDecimal> revenueByYearMonth = new TreeMap<>(); // TreeMap to keep it sorted by YearMonth
        for (Object[] row : allMonthlyResults) {
            YearMonth ym = YearMonth.of((Integer) row[0], (Integer) row[1]);
            revenueByYearMonth.put(ym, (BigDecimal) row[2]);
        }

        Map<String, BigDecimal> chartMonthlyData = new LinkedHashMap<>();
        List<String> labels = new ArrayList<>();
        List<BigDecimal> data = new ArrayList<>();

        DateTimeFormatter chartLabelFormatter = DateTimeFormatter.ofPattern("yyyy-MM");
        YearMonth currentYm = YearMonth.of(endYear, endMonth);

        for (int i = 0; i < numberOfMonths; i++) {
            String label = currentYm.format(chartLabelFormatter);
            // Add to the beginning to maintain order from oldest to newest for the chart
            labels.add(0, label);
            data.add(0, revenueByYearMonth.getOrDefault(currentYm, BigDecimal.ZERO));
            currentYm = currentYm.minusMonths(1);
        }

        Map<String, Object> chartData = new HashMap<>();
        chartData.put("labels", labels);
        chartData.put("data", data);
        return chartData;
    }

    // Các phương thức khác giữ nguyên
    public List<order_membership> getAllApprovedOrders(Long membershipId) {
        if (membershipId != null) {
            return orderMembershipRepository.findAllByMembership_MembershipIdAndStatus(membershipId, Status.APPROVED);
        }
        return orderMembershipRepository.findAllByStatus(Status.APPROVED);
    }

    public List<order_membership> getApprovedOrdersBetweenDates(Long membershipId, LocalDateTime startDate,
            LocalDateTime endDate) {
        if (membershipId != null) {
            return orderMembershipRepository.findByMembership_MembershipIdAndStatusAndCreatedAtBetween(membershipId,
                    Status.APPROVED, startDate, endDate);
        }
        return orderMembershipRepository.findByStatusAndCreatedAtBetween(Status.APPROVED, startDate, endDate);
    }

    public List<order_membership> getApprovedOrdersByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return orderMembershipRepository.findByStatusAndCreatedAtBetween(Status.APPROVED, startDate, endDate);
    }

    public List<Map<String, Object>> getRevenueByDoctorAndMembership(Long membershipId, String orderTypeParam) {
        order_membership.OrderType type = null;
        if (orderTypeParam != null && !orderTypeParam.isEmpty()) {
            try {
                type = order_membership.OrderType.valueOf(orderTypeParam);
            } catch (Exception ignored) {}
        }
        List<Object[]> results;
        if (type == null) {
            results = orderMembershipRepository.findRevenueByDoctorAndMembership(Status.APPROVED, membershipId);
        } else {
            // Chưa có hàm repo, tạm thời lọc sau khi lấy ra
            results = orderMembershipRepository.findRevenueByDoctorAndMembership(Status.APPROVED, membershipId);
        }
        return results.stream()
                .map(row -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("doctorName", row[0]);
                    map.put("membershipName", row[1]);
                    map.put("totalRevenue", row[2]);
                    return map;
                })
                .collect(Collectors.toList());
    }

    // New method to get yearly revenue data for the chart
    public Map<String, Object> getYearlyRevenueChartData(Long membershipId) {
        List<Object[]> results = orderMembershipRepository.findYearlyRevenueByStatusAndMembership(
                Status.APPROVED, membershipId);

        List<String> labels = new ArrayList<>();
        List<BigDecimal> data = new ArrayList<>();

        // Using a TreeMap to ensure years are sorted, though the query has ORDER BY
        // year
        Map<Integer, BigDecimal> yearlyDataMap = new TreeMap<>();
        if (results != null) {
            for (Object[] row : results) {
                if (row != null && row.length >= 2 && row[0] != null && row[1] != null) {
                    yearlyDataMap.put((Integer) row[0], (BigDecimal) row[1]);
                }
            }
        }

        for (Map.Entry<Integer, BigDecimal> entry : yearlyDataMap.entrySet()) {
            labels.add(entry.getKey().toString()); // Year as string
            data.add(entry.getValue());
        }

        Map<String, Object> chartData = new HashMap<>();
        chartData.put("labels", labels);
        chartData.put("data", data);
        return chartData;
    }

    // Method for the new chart: Monthly revenue by membership type for a selected
    // year
    public Map<String, Object> getMonthlyRevenueByMembershipTypeChartData(Integer year, String orderTypeParam) {
        // Hiện tại repo chưa có filter type, nên sẽ lọc sau khi lấy ra
        List<Object[]> results = orderMembershipRepository.findMonthlyRevenueByMembershipAndStatus(Status.APPROVED, null);
        order_membership.OrderType type = null;
        if (orderTypeParam != null && !orderTypeParam.isEmpty()) {
            try {
                type = order_membership.OrderType.valueOf(orderTypeParam);
            } catch (Exception ignored) {}
        }
        List<Object[]> filteredResults = (type == null) ? results : results.stream().filter(row -> {
            // row[4] là type nếu có, nếu không thì cần bổ sung vào query repo
            // Tạm thời không có, nên sẽ không filter được ở đây
            return true;
        }).collect(Collectors.toList());
        // Sử dụng lại logic cũ với filteredResults
        List<String> monthLabels = Arrays.asList(
                "Tháng 1", "Tháng 2", "Tháng 3", "Tháng 4", "Tháng 5", "Tháng 6",
                "Tháng 7", "Tháng 8", "Tháng 9", "Tháng 10", "Tháng 11", "Tháng 12");
        Map<String, BigDecimal[]> revenuesByMembershipMonth = new LinkedHashMap<>();
        Set<String> membershipNames = new TreeSet<>();
        if (filteredResults != null) {
            for (Object[] row : filteredResults) {
                if (row != null && row.length >= 4 && row[0] != null && row[1] != null && row[2] != null && row[3] != null) {
                    Integer resultYear = (Integer) row[1];
                    if (year.equals(resultYear)) {
                        String membershipName = (String) row[0];
                        int monthIndex = (Integer) row[2] - 1;
                        BigDecimal revenue = (BigDecimal) row[3];
                        membershipNames.add(membershipName);
                        revenuesByMembershipMonth.putIfAbsent(membershipName, new BigDecimal[12]);
                        if (revenuesByMembershipMonth.get(membershipName)[monthIndex] == null) {
                            revenuesByMembershipMonth.get(membershipName)[monthIndex] = BigDecimal.ZERO;
                        }
                        revenuesByMembershipMonth.get(membershipName)[monthIndex] = revenuesByMembershipMonth.get(membershipName)[monthIndex].add(revenue);
                    }
                }
            }
        }
        List<Map<String, Object>> datasets = new ArrayList<>();
        List<String> backgroundColors = Arrays.asList(
                "rgba(54, 162, 235, 0.6)", "rgba(255, 99, 132, 0.6)", "rgba(75, 192, 192, 0.6)",
                "rgba(255, 206, 86, 0.6)", "rgba(153, 102, 255, 0.6)", "rgba(255, 159, 64, 0.6)");
        List<String> borderColors = Arrays.asList(
                "rgba(54, 162, 235, 1)", "rgba(255, 99, 132, 1)", "rgba(75, 192, 192, 1)",
                "rgba(255, 206, 86, 1)", "rgba(153, 102, 255, 1)", "rgba(255, 159, 64, 1)");
        int colorIndex = 0;
        for (String name : membershipNames) {
            BigDecimal[] monthlyData = revenuesByMembershipMonth.get(name);
            for (int i = 0; i < 12; i++) {
                if (monthlyData[i] == null) {
                    monthlyData[i] = BigDecimal.ZERO;
                }
            }
            Map<String, Object> dataset = new HashMap<>();
            dataset.put("label", name);
            dataset.put("data", Arrays.asList(monthlyData));
            dataset.put("backgroundColor", backgroundColors.get(colorIndex % backgroundColors.size()));
            dataset.put("borderColor", borderColors.get(colorIndex % borderColors.size()));
            dataset.put("borderWidth", 1);
            datasets.add(dataset);
            colorIndex++;
        }
        Map<String, Object> chartData = new HashMap<>();
        chartData.put("labels", monthLabels);
        chartData.put("datasets", datasets);
        return chartData;
    }

    public void save(order_membership order_membership) {
        orderMembershipRepository.save(order_membership);
    }
}