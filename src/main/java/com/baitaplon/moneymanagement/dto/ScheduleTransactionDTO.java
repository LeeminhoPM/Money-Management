package com.baitaplon.moneymanagement.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ScheduleTransactionDTO {
    String taskName;

    String cronExpression;

    String icon;

    String name;

    BigDecimal amount;

    String type;

    String categoryId;

    String userId;
}
