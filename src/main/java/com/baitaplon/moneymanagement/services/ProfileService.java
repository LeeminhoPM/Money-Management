package com.baitaplon.moneymanagement.services;

import com.baitaplon.moneymanagement.dto.AuthDTO;
import com.baitaplon.moneymanagement.dto.ProfileDTO;
import com.baitaplon.moneymanagement.entities.ProfileEntity;
import com.baitaplon.moneymanagement.repositories.ProfileRepository;
import com.baitaplon.moneymanagement.utils.JWTUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProfileService {
    ProfileRepository profileRepository;
    EmailService emailService;
    PasswordEncoder passwordEncoder;
    AuthenticationManager authenticationManager;

    @NonFinal
    @Value("${app.activation.url}")
    String activationUrl;

    public ProfileDTO registerProfile(ProfileDTO profileDTO) {
        ProfileEntity newProfile = toEntity(profileDTO);
        newProfile.setActivationToken(UUID.randomUUID().toString());
        newProfile.setPassword(passwordEncoder.encode(newProfile.getPassword()));
        newProfile = profileRepository.save(newProfile);

//        Gửi email kích hoạt
        String activationLink = activationUrl + "/api/v1.0/activate?token=" + newProfile.getActivationToken();
        String subject = "Kích hoạt trợ lý tài chính của bạn";
        String body = "Click vào đây để kích hoạt<br>" + activationLink;
        emailService.sendEmail(newProfile.getEmail(), subject, body);

        return toDTO(newProfile);
    }

    public ProfileEntity toEntity(ProfileDTO profileDTO) {
        return ProfileEntity.builder()
                .id(profileDTO.getId())
                .fullName(profileDTO.getFullName())
                .email(profileDTO.getEmail())
                .password(profileDTO.getPassword())
                .profileImageUrl(profileDTO.getProfileImageUrl())
                .createdAt(profileDTO.getCreatedAt())
                .updatedAt(profileDTO.getUpdatedAt())
                .build();
    }

    public ProfileDTO toDTO(ProfileEntity profileEntity) {
        return ProfileDTO.builder()
                .id(profileEntity.getId())
                .fullName(profileEntity.getFullName())
                .email(profileEntity.getEmail())
                .password(profileEntity.getPassword())
                .profileImageUrl(profileEntity.getProfileImageUrl())
                .createdAt(profileEntity.getCreatedAt())
                .updatedAt(profileEntity.getUpdatedAt())
                .build();
    }

    public boolean activateProfile(String activationToken) {
        return profileRepository.findByActivationToken(activationToken).map(
                profile -> {
                    profile.setIsActive(true);
                    profileRepository.save(profile);
                    return true;
                }
        ).orElse(false);
    }

    public boolean isAccountActive(String email) {
        return profileRepository
                .findByEmail(email)
                .map(ProfileEntity::getIsActive)
                .orElse(false);
    }

//    Hàm lấy thông tin người đang đăng nhập
    public ProfileEntity getCurrentProfile() {
//        Thông tin người dùng đang đăng nhập được lưu trong SecurityContextHolder
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assert authentication != null;
//        Ở đây getName vì mặc định sẽ dùng email làm username
        return profileRepository.findByEmail(authentication.getName()).orElseThrow(
                () -> new UsernameNotFoundException("Không tìm thấy người dùng")
        );
    }

    public ProfileDTO getPublicProfile(String email) {
        ProfileEntity currentUser;
//        Nếu không có email th truyền vào user đang đăng nhập
//        Nếu có thì tìm người đó và đưa ra
        if (email == null) {
            currentUser = getCurrentProfile();
        } else {
            currentUser = profileRepository.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy người dùng"));
        }

        return ProfileDTO.builder()
                .id(currentUser.getId())
                .fullName(currentUser.getFullName())
                .email(currentUser.getEmail())
                .profileImageUrl(currentUser.getProfileImageUrl())
                .createdAt(currentUser.getCreatedAt())
                .updatedAt(currentUser.getUpdatedAt())
                .build();
    }

    public Map<String, Object> authenticateAndGenerateToken(AuthDTO authDTO) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authDTO.getEmail(), authDTO.getPassword()));
//            Sinh token
            JWTUtil jwtUtil = new JWTUtil();
            return Map.of("token", jwtUtil.generateToken(authDTO.getEmail()), "user", getPublicProfile(authDTO.getEmail()));
        } catch (Exception e) {
            throw new RuntimeException("Mật khẩu hoặc email sai");
        }
    }
}
