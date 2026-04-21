package com.hnclothing.order;

import com.hnclothing.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {


    List<Order> findByStatus(OrderStatus status);

    List<Order> findByUserOrderByCreatedAtDesc(User user);


    @Query("SELECT SUM(o.total) FROM Order o WHERE o.status = com.hnclothing.order.OrderStatus.DELIVERED")
    BigDecimal calculateTotalRevenue();




    List<Order> findByStatusAndCreatedAtBetween(OrderStatus status, Timestamp start, Timestamp end);

    @Query("SELECT MONTH(o.createdAt), SUM(o.total) FROM Order o WHERE YEAR(o.createdAt) = :year AND o.status = com.hnclothing.order.OrderStatus.DELIVERED GROUP BY MONTH(o.createdAt)")
    List<Object[]> getMonthlyRevenueByYear(@Param("year") int year);

}