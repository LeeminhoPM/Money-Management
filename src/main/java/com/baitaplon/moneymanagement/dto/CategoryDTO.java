package com.baitaplon.moneymanagement.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CategoryDTO {
    String id;
    String profileId;
    String name;
    String icon;
    String type;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
