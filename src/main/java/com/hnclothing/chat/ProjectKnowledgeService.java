package com.hnclothing.chat;

import com.hnclothing.product.Product;
import com.hnclothing.product.ProductRepository;
import org.springframework.stereotype.Service;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProjectKnowledgeService {

    private final ProductRepository productRepository;

    public ProjectKnowledgeService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public String buildKnowledge() {
        List<Product> products = productRepository.findAll();
        if (products.isEmpty()) return "Hiện không có sản phẩm.";

        // Chỉ lấy 10 sản phẩm tiêu biểu nhất
        return products.stream()
                .limit(10)
                .map(p -> String.format("%s (%sđ, %s)", p.getName(), p.getPrice(), p.getMaterial()))
                .collect(Collectors.joining(", "));
    }

    public List<Product> getRandomProducts(int n) {
        List<Product> products = productRepository.findAll();
        if (products.isEmpty()) return Collections.emptyList();
        List<Product> shuffled = new java.util.ArrayList<>(products);
        Collections.shuffle(shuffled);
        return shuffled.stream().limit(n).collect(Collectors.toList());
    }

    public String formatProducts(List<Product> products) {
        return products.stream()
                .map(p -> "• " + p.getName() + ": " + p.getPrice() + "₫")
                .collect(Collectors.joining("\n"));
    }
}