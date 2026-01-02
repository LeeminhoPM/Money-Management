package com.baitaplon.moneymanagement.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProfileDTO {
    String id;
    String fullName;
    String email;
    String password;
    String profileImageUrl;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
