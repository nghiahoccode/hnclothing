package com.hnclothing.controller;

import com.hnclothing.order.OrderRepository;
import com.hnclothing.order.OrderService;
import com.hnclothing.order.OrderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminHomeController {

    private final OrderService orderService;
    private final OrderRepository orderRepository;

    @GetMapping("/home")
    public String showHomePage(
            @RequestParam(name = "year", required = false) Integer year,
            Model model) {

        int selectedYear = (year == null) ? LocalDate.now().getYear() : year;

        model.addAttribute("selectedYear", selectedYear);
        model.addAttribute("revenueData", orderService.getRevenueByYear(selectedYear));
        model.addAttribute("productSales", orderService.getProductSales());

        return "admin/home";
    }
}