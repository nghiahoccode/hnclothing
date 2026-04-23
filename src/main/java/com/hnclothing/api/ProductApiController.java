package com.hnclothing.api;

import com.hnclothing.product.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductApiController {

    private final ProductService productService;

    @GetMapping("/featured")
    public ResponseEntity<?> getFeatured() {
        return ResponseEntity.ok(productService.getFeaturedProducts());
    }

    @GetMapping("/category/{id}")
    public ResponseEntity<?> getByCategory(@PathVariable Integer id) {
        return ResponseEntity.ok(productService.findByCategoryId(id));
    }
}