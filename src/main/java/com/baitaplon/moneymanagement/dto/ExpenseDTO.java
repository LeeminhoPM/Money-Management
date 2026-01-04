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
public class ExpenseDTO {
    String id;
    String name;
    String icon;
    String categoryName;
    String categoryId;
    BigDecimal amount;
    LocalDate date;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
