package com.baitaplon.moneymanagement.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdatedProfileDTO {
    String fullName;
    String oldPassword;
    String newPassword;
    String profileImageUrl;
}
