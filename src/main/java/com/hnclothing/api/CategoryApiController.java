package com.hnclothing.api;

import com.hnclothing.category.CategoryDTO;
import com.hnclothing.category.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/categories")
@RequiredArgsConstructor
public class CategoryApiController {

    private final CategoryService categoryService;

    @GetMapping("/{id}")
    public ResponseEntity<CategoryDTO> getDetail(@PathVariable Integer id) {
        return ResponseEntity.ok(categoryService.getCategoryById(id));
    }

    @PostMapping("/add")
    public ResponseEntity<?> create(@RequestBody CategoryDTO dto) {
        categoryService.createCategory(dto);
        return ResponseEntity.ok(Map.of("message", "Tạo danh mục thành công"));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.ok(Map.of("message", "Xóa thành công"));
    }
}