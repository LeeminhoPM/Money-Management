package com.baitaplon.moneymanagement.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FilterDTO {
    String type;
    LocalDate startDate;
    LocalDate endDate;
    String keyword;
    String sortField;
    String sortOrder;
}
