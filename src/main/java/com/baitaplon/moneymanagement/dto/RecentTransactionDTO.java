package com.baitaplon.moneymanagement.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RecentTransactionDTO {
    String id;
    String profileId;
    String icon;
    String name;
    BigDecimal amount;
    LocalDate date;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    String type;
}
