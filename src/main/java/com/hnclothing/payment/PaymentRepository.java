package com.hnclothing.payment;

import com.hnclothing.order.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Integer> {

    // Tìm kiếm thông tin thanh toán dựa trên đơn hàng
    Optional<Payment> findByOrder(Order order);

    // Tìm kiếm nhanh dựa trên Order ID (hữu ích cho Webhook)
    Optional<Payment> findByOrderId(Integer orderId);

}