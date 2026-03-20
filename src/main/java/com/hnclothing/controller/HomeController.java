package com.hnclothing.controller;

import com.hnclothing.cart.CartService;
import com.hnclothing.category.CategoryDTO;
import com.hnclothing.category.CategoryService;
import com.hnclothing.product.ProductService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final ProductService productService;
    private final CategoryService categoryService;
    private final CartService cartService;

    // --- LOGIC TẢI DỮ LIỆU CHUNG (Navbar) ---


    @ModelAttribute("categories")
    public List<CategoryDTO> getCategories() {
        return categoryService.getAllCategories();
    }

    @ModelAttribute("cartItemCount")
    public int getCartItemCount(HttpServletRequest request, HttpServletResponse response) {
        return cartService.countItemsInCart(request, response);
    }

    // --- CÁC TRANG CƠ BẢN ---

    @GetMapping({"/", "/user/home", "/user/"})
    public String home(Model model) {
        model.addAttribute("featuredProducts", productService.getFeaturedProducts());
        return "user/home";
    }

    // Trang lọc theo danh mục
    @GetMapping("/products/by-category/{id}")
    public String productByCategory(@PathVariable Integer id, Model model) {
        model.addAttribute("products", productService.findByCategoryId(id));
        model.addAttribute("pageTitle", "Sản phẩm theo danh mục");
        // Reuse view danh sách sản phẩm
        return "user/product";
    }

    @GetMapping("/user/about")
    public String about() { return "user/about"; }

    @GetMapping("/user/contact")
    public String contact() { return "user/contact"; }
}