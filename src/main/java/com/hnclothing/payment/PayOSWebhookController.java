package com.hnclothing.payment;

import com.hnclothing.order.OrderRepository;
import com.hnclothing.order.OrderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payos")
@RequiredArgsConstructor
public class PayOSWebhookController {

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;

    @PostMapping("/webhook")
    public void handleWebhook(@RequestBody Map<String, Object> body) {

        try {

            Map<String, Object> data = (Map<String, Object>) body.get("data");

            if (data == null) {
                return;
            }

            Integer orderId = ((Number) data.get("orderCode")).intValue();
            String code = (String) body.get("code");

            if ("00".equals(code)) {
                updatePaymentStatus(orderId, PaymentStatus.PAID);
            }

        } catch (Exception e) {
            System.err.println("Webhook lỗi: " + e.getMessage());
        }
    }

    private void updatePaymentStatus(Integer orderId, PaymentStatus status) {

        orderRepository.findById(orderId).ifPresent(order -> {

            Payment payment = order.getPayment();

            if (payment != null) {

                payment.setStatus(status);
                paymentRepository.save(payment);

                if (status == PaymentStatus.PAID &&
                        order.getStatus() == OrderStatus.PENDING) {

                    order.setStatus(OrderStatus.PROCESSING);
                    orderRepository.save(order);
                }
            }
        });
    }
}