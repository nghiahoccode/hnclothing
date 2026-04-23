package com.hnclothing.api;

import com.hnclothing.cart.Cart;
import com.hnclothing.cart.CartService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartApiController {

    private final CartService cartService;

    @GetMapping
    public ResponseEntity<?> getCart(HttpServletRequest request, HttpServletResponse response) {
        Cart cart = cartService.getCart(request, response);
        return ResponseEntity.ok(cart);
    }

    @PostMapping("/add")
    public ResponseEntity<?> addToCart(@RequestBody Map<String, Object> payload,
                                       HttpServletRequest request, HttpServletResponse response) {
        Integer productId = (Integer) payload.get("productId");
        Integer quantity = (Integer) payload.get("quantity");
        String size = (String) payload.get("size");
        String color = (String) payload.get("color");

        cartService.addToCart(productId, quantity, size, color, request, response);
        return ResponseEntity.ok(Map.of("message", "Thêm vào giỏ hàng thành công"));
    }

    @PostMapping("/update-quantity")
    public ResponseEntity<?> updateQuantity(@RequestBody Map<String, Integer> payload,
                                            HttpServletRequest request, HttpServletResponse response) {
        cartService.updateQuantity(payload.get("cartItemId"), payload.get("newQuantity"), request, response);
        return ResponseEntity.ok(Map.of("message", "Cập nhật số lượng thành công"));
    }

    @DeleteMapping("/remove/{id}")
    public ResponseEntity<?> removeFromCart(@PathVariable Integer id, HttpServletRequest request, HttpServletResponse response) {
        cartService.removeFromCart(id, request, response);
        return ResponseEntity.ok(Map.of("message", "Đã xóa sản phẩm khỏi giỏ"));
    }
}