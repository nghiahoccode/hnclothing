package com.hnclothing.order;

import com.hnclothing.user.User;
import com.hnclothing.vouchers.Voucher;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

@Entity
@Table(name = "orders")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "order_code", unique = true, nullable = false)
    private String orderCode; // Mã đơn hàng (VD: CM20231234)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id") // Cho phép null nếu bạn muốn hỗ trợ khách vãng lai sau này
    private User user;

    // --- LOGIC VOUCHER ---
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "voucher_id")
    private Voucher voucher;

    @Column(name = "discount_amount", columnDefinition = "DECIMAL(10,2) DEFAULT 0")
    private BigDecimal discountAmount;

    // --- LOGIC GIÁ ---
    // 1. Tổng tiền hàng (Chưa giảm giá, chưa tính phí ship)
    @Column(name = "sub_total", nullable = false, precision = 10, scale = 2)
    private BigDecimal subTotal;

    // 2. Tổng tiền thanh toán cuối cùng (Đã trừ giảm giá + Phí ship (nếu có))

    @Column(name = "total", nullable = false, precision = 10, scale = 2)
    private BigDecimal total; // Tên trường này tương đương với FINAL_TOTAL


    @Column(name = "shipping_fee", columnDefinition = "DECIMAL(10,2) DEFAULT 0")
    private BigDecimal shippingFee = BigDecimal.ZERO;


    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private OrderStatus status;

    // --- THÔNG TIN GIAO HÀNG (SNAPSHOT) ---
    @Column(name = "recipient_name", nullable = false)
    private String recipientName;

    @Column(name = "recipient_phone", nullable = false)
    private String recipientPhone;

    @Column(name = "delivery_address", nullable = false, length = 500)
    private String deliveryAddress;

    @Column(name = "note")
    private String note;

    @Column(name = "payment_method")
    private String paymentMethod;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Timestamp createdAt;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems;

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private com.hnclothing.payment.Payment payment;
}