package com.hnclothing.api;

import com.hnclothing.sizes.SizeDTO;
import com.hnclothing.sizes.SizesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/sizes")
@RequiredArgsConstructor
public class AdminSizeApiController {

    private final SizesService sizesService;

    @GetMapping
    public ResponseEntity<List<SizeDTO>> list() {
        return ResponseEntity.ok(sizesService.getAllSizes());
    }

    @PostMapping("/save")
    public ResponseEntity<?> save(@RequestBody SizeDTO sizeDTO) {
        sizesService.saveSize(sizeDTO);
        return ResponseEntity.ok(Map.of("message", "Lưu size thành công"));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        sizesService.deleteSize(id);
        return ResponseEntity.ok(Map.of("message", "Xóa size thành công"));
    }
}