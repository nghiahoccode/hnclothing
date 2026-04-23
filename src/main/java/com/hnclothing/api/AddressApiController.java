package com.hnclothing.api;

import com.hnclothing.address.Address;
import com.hnclothing.address.AddressRepository;
import com.hnclothing.user.User;
import com.hnclothing.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user/address")
@RequiredArgsConstructor
public class AddressApiController {

    private final AddressRepository addressRepository;
    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<Address>> listAddresses(Principal principal) {
        User user = userService.getUserEntityByEmail(principal.getName());
        return ResponseEntity.ok(addressRepository.findByUser(user));
    }

    @PostMapping("/add")
    public ResponseEntity<?> addAddress(@RequestBody Map<String, String> req, Principal principal) {
        User user = userService.getUserEntityByEmail(principal.getName());
        String fullAddressStr = String.format("%s, %s, %s, %s", req.get("street"), req.get("ward"), req.get("district"), req.get("city"));

        Address address = Address.builder()
                .user(user)
                .fullName(req.get("fullName"))
                .phone(req.get("phone"))
                .address(fullAddressStr)
                .isDefault(addressRepository.findByUser(user).isEmpty() ? 1 : 0)
                .build();

        return ResponseEntity.ok(addressRepository.save(address));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteAddress(@PathVariable Integer id, Principal principal) {
        User user = userService.getUserEntityByEmail(principal.getName());
        Address address = addressRepository.findById(id).orElse(null);
        if (address != null && address.getUser().getId().equals(user.getId())) {
            addressRepository.deleteById(id);
            return ResponseEntity.ok(Map.of("message", "Xóa thành công"));
        }
        return ResponseEntity.badRequest().body("Không có quyền xóa");
    }
}