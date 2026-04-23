package com.hnclothing.api;

import com.hnclothing.order.OrderDTO;
import com.hnclothing.order.OrderService;
import com.hnclothing.order.OrderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/orders")
@RequiredArgsConstructor
public class AdminOrderApiController {

    private final OrderService orderService;

    @GetMapping
    public ResponseEntity<List<OrderDTO>> getAllOrders(@RequestParam(value = "status", required = false) String status) {
        return ResponseEntity.ok(orderService.getAllOrders(status));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDTO> getOrderDetail(@PathVariable Integer id) {
        return ResponseEntity.ok(orderService.getOrderDetail(id));
    }

    @PostMapping("/update-status")
    public ResponseEntity<?> updateStatus(@RequestBody Map<String, Object> req) {
        Integer orderId = (Integer) req.get("id");
        String status = (String) req.get("status");
        orderService.updateOrderStatus(orderId, OrderStatus.valueOf(status.toUpperCase()));
        return ResponseEntity.ok(Map.of("message", "Cập nhật trạng thái đơn hàng thành công"));
    }
}