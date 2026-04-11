package com.hnclothing.address;

import com.hnclothing.user.User;
import com.hnclothing.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/user/address")
@RequiredArgsConstructor
public class AddressController {

    private final AddressRepository addressRepository;
    private final UserService userService;

    // 1. READ
    @GetMapping
    public String listAddresses(Model model, Principal principal) {
        User user = userService.getUserEntityByEmail(principal.getName());
        List<Address> addresses = addressRepository.findByUser(user);
        model.addAttribute("addresses", addresses);
        model.addAttribute("activeTab", "address");
        return "user/address";
    }

    // 2. CREATE
    @PostMapping("/add")
    public String addAddress(
            @RequestParam("fullName") String fullName,
            @RequestParam("phone") String phone,
            @RequestParam("city") String city,
            @RequestParam("district") String district,
            @RequestParam("ward") String ward,
            @RequestParam("street") String street,
            Principal principal,
            RedirectAttributes redirectAttributes) {

        User user = userService.getUserEntityByEmail(principal.getName());
        String fullAddressStr = String.format("%s, %s, %s, %s", street, ward, district, city);

        Address address = Address.builder()
                .user(user)
                .fullName(fullName)
                .phone(phone)
                .address(fullAddressStr)
                .isDefault(0)
                .build();

        if (addressRepository.findByUser(user).isEmpty()) {
            address.setIsDefault(1);
        }

        addressRepository.save(address);
        redirectAttributes.addFlashAttribute("successMessage", "Thêm địa chỉ thành công!");
        return "redirect:/user/address";
    }

    // --- 3. UPDATE: Cập nhật địa chỉ
    @PostMapping("/update")
    public String updateAddress(
            @RequestParam("id") Integer id, // ID của địa chỉ cần sửa
            @RequestParam("fullName") String fullName,
            @RequestParam("phone") String phone,
            @RequestParam("city") String city,
            @RequestParam("district") String district,
            @RequestParam("ward") String ward,
            @RequestParam("street") String street,
            Principal principal,
            RedirectAttributes redirectAttributes) {

        User user = userService.getUserEntityByEmail(principal.getName());
        Address address = addressRepository.findById(id).orElse(null);

        // Kiểm tra quyền sở hữu: chỉ cho sửa nếu địa chỉ đó thuộc về user đang login
        if (address != null && address.getUser().getId().equals(user.getId())) {

            // Cập nhật thông tin cơ bản
            address.setFullName(fullName);
            address.setPhone(phone);

            // Gộp lại địa chỉ mới và lưu
            String fullAddressStr = String.format("%s, %s, %s, %s", street, ward, district, city);
            address.setAddress(fullAddressStr);

            addressRepository.save(address);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật địa chỉ thành công!");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy địa chỉ hoặc bạn không có quyền sửa!");
        }

        return "redirect:/user/address";
    }

    // 4. DELETE
    @GetMapping("/delete/{id}")
    public String deleteAddress(@PathVariable Integer id, Principal principal, RedirectAttributes redirectAttributes) {
        User user = userService.getUserEntityByEmail(principal.getName());
        Address address = addressRepository.findById(id).orElse(null);

        if (address != null && address.getUser().getId().equals(user.getId())) {
            addressRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Xóa địa chỉ thành công!");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi xóa địa chỉ!");
        }
        return "redirect:/user/address";
    }
}