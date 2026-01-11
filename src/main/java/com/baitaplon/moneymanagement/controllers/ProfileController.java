package com.baitaplon.moneymanagement.controllers;

import com.baitaplon.moneymanagement.dto.AuthDTO;
import com.baitaplon.moneymanagement.dto.ProfileDTO;
import com.baitaplon.moneymanagement.services.ProfileService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProfileController {
    ProfileService profileService;

    @PostMapping("/register")
    public ResponseEntity<ProfileDTO> toDTO(@RequestBody ProfileDTO profileDTO) {
        ProfileDTO registerProfile = profileService.registerProfile(profileDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(registerProfile);
    }

    @GetMapping("/activate")
    public ResponseEntity<String> activateProfile(@RequestParam String token) {
        boolean isActivated = profileService.activateProfile(token);
        if (isActivated) {
            return ResponseEntity.status(HttpStatus.OK).body("Kích hoạt thành công");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Kích hoạt không thành công");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody AuthDTO authDTO) {
        try {
            if (!profileService.isAccountActive(authDTO.getEmail())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                        "message", "Tài khoản chưa được kích hoạt, vui lòng kích hoạt tài khoản"
                ));
            }
            Map<String, Object> response = profileService.authenticateAndGenerateToken(authDTO);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/profile")
    public ResponseEntity<ProfileDTO> getProfile() {
        ProfileDTO response = profileService.getPublicProfile(null);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
