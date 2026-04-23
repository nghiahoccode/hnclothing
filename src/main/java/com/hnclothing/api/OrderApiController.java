package com.hnclothing.api;

import com.hnclothing.order.Order;
import com.hnclothing.order.OrderService;
import com.hnclothing.order.OrderDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.payos.PayOS;
import vn.payos.model.v2.paymentRequests.CreatePaymentLinkRequest;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderApiController {

    private final OrderService orderService;
    private final PayOS payOS;

    @PostMapping("/place")
    public ResponseEntity<?> placeOrder(@RequestBody Map<String, Object> req,
                                        HttpServletRequest request, HttpServletResponse response, Principal principal) throws Exception {

        Order order = orderService.createOrder(
                (String) req.get("fullName"), (String) req.get("phone"), (String) req.get("addressDetail"),
                (String) req.get("orderNotes"), (String) req.get("paymentMethod"),
                request, response, principal, (String) req.get("voucherCode"),
                new BigDecimal(req.getOrDefault("discountAmount", 0).toString())
        );

        if ("PAYOS".equalsIgnoreCase((String) req.get("paymentMethod"))) {
            String baseUrl = request.getRequestURL().toString().replace(request.getRequestURI(), "");
            CreatePaymentLinkRequest payRequest = CreatePaymentLinkRequest.builder()
                    .orderCode(order.getId().longValue())
                    .amount(order.getTotal().longValue())
                    .description("DH" + order.getId())
                    .returnUrl(baseUrl + "/api/orders/success/" + order.getId())
                    .cancelUrl(baseUrl + "/api/orders/cancel")
                    .build();
            return ResponseEntity.ok(Map.of("checkoutUrl", payOS.paymentRequests().create(payRequest).getCheckoutUrl()));
        }
        return ResponseEntity.ok(order);
    }

    @GetMapping("/my-orders")
    public ResponseEntity<?> getMyOrders(Principal principal) {
        return ResponseEntity.ok(orderService.getMyOrders(principal.getName()));
    }
}