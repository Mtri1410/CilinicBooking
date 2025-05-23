package com.example.bai1.Repository;

import com.example.bai1.Model.order_membership;
import com.example.bai1.Model.order_membership.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.math.BigDecimal;

@Repository
public interface OrderMembershipRepository extends JpaRepository<order_membership, Long> {

       List<order_membership> findByStatus(Status status);

       // Find approved orders within a date range
       List<order_membership> findByStatusAndCreatedAtBetween(Status status, LocalDateTime startDate,
                     LocalDateTime endDate);

       List<order_membership> findByMembership_MembershipIdAndStatusAndCreatedAtBetween(Long membershipId,
                     Status status, LocalDateTime startDate, LocalDateTime endDate);

       // Find all approved orders (useful for total lifetime revenue)
       List<order_membership> findAllByStatus(Status status);

       List<order_membership> findAllByMembership_MembershipIdAndStatus(Long membershipId, Status status);

       // Example: Sum of totalprice for approved orders by month
       @Query("SELECT FUNCTION('YEAR', om.createdAt) as year, FUNCTION('MONTH', om.createdAt) as month, SUM(om.totalprice) as monthlyRevenue "
                     +
                     "FROM order_membership om " +
                     "WHERE om.status = :status " +
                     "AND (:membershipId IS NULL OR om.membership.membershipId = :membershipId) " +
                     "GROUP BY FUNCTION('YEAR', om.createdAt), FUNCTION('MONTH', om.createdAt) " +
                     "ORDER BY year, month")
       List<Object[]> findMonthlyRevenueByStatusAndMembership(@Param("status") Status status,
                     @Param("membershipId") Long membershipId);

       // Example: Sum of totalprice for approved orders by year
       @Query("SELECT FUNCTION('YEAR', om.createdAt) as year, SUM(om.totalprice) as yearlyRevenue " +
                     "FROM order_membership om " +
                     "WHERE om.status = :status " +
                     "AND (:membershipId IS NULL OR om.membership.membershipId = :membershipId) " +
                     "GROUP BY FUNCTION('YEAR', om.createdAt) " +
                     "ORDER BY year")
       List<Object[]> findYearlyRevenueByStatusAndMembership(@Param("status") Status status,
                     @Param("membershipId") Long membershipId);

       // Sum of totalprice for approved orders by membership type and month
       // This query already groups by membership name. If a membershipId is provided,
       // it will filter for that specific membership.
       @Query("SELECT m.name as membershipName, FUNCTION('YEAR', om.createdAt) as year, FUNCTION('MONTH', om.createdAt) as month, SUM(om.totalprice) as monthlyRevenue "
                     +
                     "FROM order_membership om JOIN om.membership m " +
                     "WHERE om.status = :status " +
                     "AND (:membershipId IS NULL OR m.membershipId = :membershipId) " +
                     "GROUP BY m.name, FUNCTION('YEAR', om.createdAt), FUNCTION('MONTH', om.createdAt) " +
                     "ORDER BY m.name, year, month")
       List<Object[]> findMonthlyRevenueByMembershipAndStatus(@Param("status") Status status,
                     @Param("membershipId") Long membershipId);

       // New query to get revenue per doctor, optionally filtered by membership
       @Query("SELECT d.fullname as doctorName, m.name as membershipName, SUM(om.totalprice) as totalRevenue " +
                     "FROM order_membership om JOIN om.doctor d JOIN om.membership m " +
                     "WHERE om.status = :status " +
                     "AND (:membershipId IS NULL OR m.membershipId = :membershipId) " +
                     "GROUP BY d.fullname, m.name " +
                     "ORDER BY doctorName, membershipName")
       List<Object[]> findRevenueByDoctorAndMembership(@Param("status") Status status,
                     @Param("membershipId") Long membershipId);

       // Find approved orders for a specific year and month, optionally filtered by
       // membership
       @Query("SELECT om FROM order_membership om " +
                     "WHERE om.status = :status " +
                     "AND FUNCTION('YEAR', om.createdAt) = :year " +
                     "AND FUNCTION('MONTH', om.createdAt) = :month " +
                     "AND (:membershipId IS NULL OR om.membership.membershipId = :membershipId) " +
                     "ORDER BY om.createdAt DESC")
       List<order_membership> findByStatusAndYearAndMonthAndOptionalMembership(
                     @Param("status") Status status,
                     @Param("year") int year,
                     @Param("month") int month,
                     @Param("membershipId") Long membershipId);

       // Query to get distinct year-months from order_membership for filter dropdown
       @Query("SELECT DISTINCT CONCAT(FUNCTION('YEAR', om.createdAt), '-', LPAD(CAST(FUNCTION('MONTH', om.createdAt) AS string), 2, '0')) as yearMonth "
                     +
                     "FROM order_membership om " +
                     "ORDER BY yearMonth DESC")
       List<String> findDistinctYearMonths();

       Page<order_membership> findByDoctor_FullnameContainingIgnoreCase(String fullname, Pageable pageable);
       Page<order_membership> findByStatus(Status status, Pageable pageable);
       Page<order_membership> findByMembership_MembershipId(Long membershipId, Pageable pageable);
       Page<order_membership> findByDoctor_FullnameContainingIgnoreCaseAndStatus(String fullname, Status status, Pageable pageable);
       Page<order_membership> findByDoctor_FullnameContainingIgnoreCaseAndMembership_MembershipId(String fullname, Long membershipId, Pageable pageable);
       Page<order_membership> findByStatusAndMembership_MembershipId(Status status, Long membershipId, Pageable pageable);
       Page<order_membership> findByDoctor_FullnameContainingIgnoreCaseAndStatusAndMembership_MembershipId(String fullname, Status status, Long membershipId, Pageable pageable);
       Page<order_membership> findByDoctor_FullnameContainingIgnoreCaseAndStatusAndMembership_MembershipIdAndType(
           String fullname, Status status, Long membershipId, order_membership.OrderType type, Pageable pageable);
       Page<order_membership> findByDoctor_FullnameContainingIgnoreCaseAndStatusAndType(
           String fullname, Status status, order_membership.OrderType type, Pageable pageable);
       Page<order_membership> findByDoctor_FullnameContainingIgnoreCaseAndMembership_MembershipIdAndType(
           String fullname, Long membershipId, order_membership.OrderType type, Pageable pageable);
       Page<order_membership> findByStatusAndMembership_MembershipIdAndType(
           Status status, Long membershipId, order_membership.OrderType type, Pageable pageable);
       Page<order_membership> findByDoctor_FullnameContainingIgnoreCaseAndType(
           String fullname, order_membership.OrderType type, Pageable pageable);
       Page<order_membership> findByStatusAndType(
           Status status, order_membership.OrderType type, Pageable pageable);
       Page<order_membership> findByMembership_MembershipIdAndType(
           Long membershipId, order_membership.OrderType type, Pageable pageable);
       Page<order_membership> findByType(
           order_membership.OrderType type, Pageable pageable);

}