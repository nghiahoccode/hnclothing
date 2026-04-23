package com.hnclothing.api;

import com.hnclothing.vouchers.VoucherDTO;
import com.hnclothing.vouchers.VoucherService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/vouchers")
@RequiredArgsConstructor
public class AdminVoucherApiController {

    private final VoucherService voucherService;

    @PostMapping("/save")
    public ResponseEntity<?> save(@RequestBody VoucherDTO voucherDTO) {
        voucherService.saveVoucher(voucherDTO);
        return ResponseEntity.ok(Map.of("message", "Lưu voucher thành công"));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        voucherService.deleteVoucher(id);
        return ResponseEntity.ok(Map.of("message", "Xóa voucher thành công"));
    }
}