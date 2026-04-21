package com.hnclothing.order;

import com.hnclothing.exception.AppException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/orders")
public class OrderController {

    private final OrderService orderService;

    // --- 1. READ: Hiển thị danh sách Đơn hàng (Đã sửa logic lọc) ---

    @GetMapping
    public String listOrders(
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "detailId", required = false) Integer detailId,
            Model model) {

        List<OrderDTO> orders = orderService.getAllOrders(status);
        model.addAttribute("orders", orders);
        model.addAttribute("currentStatus", status != null ? status.toUpperCase() : "ALL");

        if (detailId != null) {
            try {
                OrderDTO orderDetail = orderService.getOrderDetail(detailId);
                model.addAttribute("orderDetail", orderDetail);
                // Đảm bảo dòng này luôn chạy khi lấy được orderDetail
                model.addAttribute("showModal", true);
            } catch (AppException e) {
                model.addAttribute("errorMessage", "Không tìm thấy chi tiết đơn hàng.");
            }
        }
        return "admin/orders";
    }

    // --- 2. UPDATE: Cập nhật trạng thái đơn hàng (Sử dụng cho form NEXT) ---
    @PostMapping("/update-status")
    public String updateStatus(
            @RequestParam("id") Integer orderId,
            @RequestParam("status") String status,
            RedirectAttributes redirectAttributes) {

        try {
            OrderStatus newStatus = OrderStatus.valueOf(status.toUpperCase());
            orderService.updateOrderStatus(orderId, newStatus);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật trạng thái đơn hàng **#" + orderId + "** thành **" + newStatus.name() + "** thành công.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Trạng thái không hợp lệ: " + status);
        } catch (AppException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }


        return "redirect:/admin/orders";
    }





}