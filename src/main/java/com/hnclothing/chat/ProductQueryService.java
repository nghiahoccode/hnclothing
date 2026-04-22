package com.hnclothing.chat;

import com.hnclothing.product.Product;
import com.hnclothing.product.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductQueryService {

    private final ProductRepository productRepository;

    public ProductQueryService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public String buildProductData() {
        List<Product> products = productRepository.findAll();

        return products.stream()
                .limit(20)
                .map(p -> String.format(
                        "ID:%d | %s | %s | %sđ",
                        p.getId(),
                        p.getName(),
                        p.getMaterial(),
                        p.getPrice()
                ))
                .collect(Collectors.joining("\n"));
    }

    public Product findById(Integer id) {
        return productRepository.findById(id).orElse(null);
    }
}