package com.hnclothing.order;

import com.hnclothing.address.Address;
import com.hnclothing.address.AddressRepository;
import com.hnclothing.cart.Cart;
import com.hnclothing.cart.CartService;
import com.hnclothing.exception.AppException;
import com.hnclothing.user.User;
import com.hnclothing.user.UserRepository;
import com.hnclothing.vouchers.Voucher;
import com.hnclothing.vouchers.VoucherService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;

// Import các class mới của SDK 2.0.1
import vn.payos.PayOS;
import vn.payos.model.v2.paymentRequests.CreatePaymentLinkRequest;

@Controller
@RequiredArgsConstructor
public class CheckoutController {

    private final CartService cartService;
    private final OrderService orderService;
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final VoucherService voucherService;
    private final PayOS payOS;



    @GetMapping("/user/checkout")
    public String showCheckoutPage(Model model, HttpServletRequest request, HttpServletResponse response, Principal principal) {
        Cart cart = cartService.getCart(request, response);
        if (cart == null || cart.getCartItems() == null || cart.getCartItems().isEmpty()) {
            return "redirect:/user/cart";
        }

        BigDecimal total = cart.getCartItems().stream()
                .map(item -> item.getPriceAtTime().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (principal != null) {
            User user = userRepository.findByEmail(principal.getName()).orElse(null);
            if (user != null) {
                model.addAttribute("userFullName", user.getFullName());
                model.addAttribute("userPhone", user.getPhone());
                model.addAttribute("userEmail", user.getEmail());
                List<Address> savedAddresses = addressRepository.findByUser(user);
                model.addAttribute("savedAddresses", savedAddresses);
                List<Voucher> myVouchers = voucherService.getAvailableVouchers();
                model.addAttribute("myVouchers", myVouchers);
            }
        }
        model.addAttribute("cart", cart);
        model.addAttribute("total", total);
        return "user/checkout";
    }

    @PostMapping("/place-order")
    public String placeOrder(
            @RequestParam("fullName") String fullName,
            @RequestParam("phone") String phone,
            @RequestParam("addressDetail") String addressDetail,
            @RequestParam(value = "orderNotes", required = false) String orderNotes,
            @RequestParam("paymentMethod") String paymentMethod,
            @RequestParam(value = "voucherCode", required = false) String voucherCode,
            @RequestParam(value = "discountAmount", required = false) BigDecimal discountAmount,
            HttpServletRequest request,
            HttpServletResponse response,
            Principal principal,
            RedirectAttributes redirectAttributes) {

        try {
            if (discountAmount == null) discountAmount = BigDecimal.ZERO;

            Order order = orderService.createOrder(
                    fullName, phone, addressDetail, orderNotes, paymentMethod,
                    request, response, principal, voucherCode, discountAmount
            );

            if ("PAYOS".equalsIgnoreCase(paymentMethod)) {
                try {
                    String baseUrl = request.getRequestURL().toString().replace(request.getRequestURI(), request.getContextPath());
                    long orderCodePayOS = order.getId().longValue();
                    long amount = order.getTotal().longValue();
                    String description = "DH " + order.getId();
                    String returnUrl = baseUrl + "/user/order_success/" + order.getId();
                    String cancelUrl = baseUrl + "/user/checkout?payment_error=true";

                    // SỬ DỤNG CreatePaymentLinkRequest CHO BẢN 2.x
                    CreatePaymentLinkRequest paymentRequest = CreatePaymentLinkRequest.builder()
                            .orderCode(orderCodePayOS)
                            .amount(amount)
                            .description(description)
                            .returnUrl(returnUrl)
                            .cancelUrl(cancelUrl)
                            .build();

                    // Gọi hàm create thông qua paymentRequests()
                    var checkoutLink = payOS.paymentRequests().create(paymentRequest);
                    return "redirect:" + checkoutLink.getCheckoutUrl();

                } catch (Exception e) {
                    e.printStackTrace();
                    redirectAttributes.addFlashAttribute("errorMessage", "Lỗi PayOS: " + e.getMessage());
                    return "redirect:/user/checkout";
                }
            }
            return "redirect:/user/order_success/" + order.getId();
        } catch (AppException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/user/checkout";
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Đã xảy ra lỗi hệ thống.");
            return "redirect:/user/checkout";
        }
    }

    @GetMapping("/api/voucher/check")
    @ResponseBody
    public ResponseEntity<?> checkVoucher(@RequestParam String code, @RequestParam BigDecimal total) {
        try {
            Voucher voucher = voucherService.validateVoucher(code, total);
            BigDecimal discount = voucherService.calculateDiscount(voucher, total);
            return ResponseEntity.ok(new VoucherResponse(true, voucher.getCode(), discount, "Áp dụng thành công!"));
        } catch (Exception e) {
            return ResponseEntity.ok(new VoucherResponse(false, null, BigDecimal.ZERO, e.getMessage()));
        }
    }

    @Data
    @AllArgsConstructor
    static class VoucherResponse {
        private boolean success;
        private String code;
        private BigDecimal discountAmount;
        private String message;
    }


    @GetMapping("/user/my_orders") // Đường dẫn chính thống hiện tại
    public String showMyOrders(Principal principal, Model model) {
        if (principal == null) return "redirect:/login";

        List<OrderDTO> orders = orderService.getMyOrders(principal.getName());
        model.addAttribute("orders", orders);
        model.addAttribute("activeTab", "order");
        return "user/my_orders";
    }

    @GetMapping("/user/order_success/{id}")
    public String orderSuccess(@PathVariable Integer id, Model model) {
        try {
            OrderDTO orderDTO = orderService.getOrderDetail(id);
            model.addAttribute("order", orderDTO);
            model.addAttribute("orderDetails", orderDTO.getOrderItems());
            return "user/order_success";
        } catch (Exception e) {
            return "redirect:/";
        }
    }

    @PostMapping("/user/order/cancel/{id}")
    public String cancelOrder(@PathVariable Integer id, Principal principal, RedirectAttributes redirectAttributes) {
        try {
            orderService.cancelOrder(id, principal);
            redirectAttributes.addFlashAttribute("successMessage", "Hủy đơn hàng thành công.");
        } catch (AppException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Đã xảy ra lỗi khi hủy đơn hàng.");
        }

        return "redirect:/user/my_orders";
    }





}