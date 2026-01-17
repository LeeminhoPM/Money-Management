package com.baitaplon.moneymanagement.controllers;

import com.baitaplon.moneymanagement.dto.ExpenseDTO;
import com.baitaplon.moneymanagement.dto.FilterDTO;
import com.baitaplon.moneymanagement.dto.IncomeDTO;
import com.baitaplon.moneymanagement.dto.RecentTransactionDTO;
import com.baitaplon.moneymanagement.services.DashboardService;
import com.baitaplon.moneymanagement.services.ExpenseService;
import com.baitaplon.moneymanagement.services.IncomeService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@RestController
@RequiredArgsConstructor
@RequestMapping("/filter")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FilterController {
    ExpenseService expenseService;
    IncomeService incomeService;
    DashboardService dashboardService;

    @GetMapping
    public ResponseEntity<List<RecentTransactionDTO>> getAllTransactions() {
        List<RecentTransactionDTO> transactions = dashboardService.getTransactionsData();
        return ResponseEntity.status(HttpStatus.OK).body(transactions);
    }

    @PostMapping
    public ResponseEntity<?> filterTransaction(@RequestBody FilterDTO filterDTO) {
        LocalDate startDate = filterDTO.getStartDate() != null ? filterDTO.getStartDate() : LocalDate.MIN;
        LocalDate endDate = filterDTO.getEndDate() != null ? filterDTO.getEndDate() : LocalDate.now();
        String keyword = filterDTO.getKeyword() != null ? filterDTO.getKeyword() : "";

        String sortField = filterDTO.getSortField() != null ? filterDTO.getSortField() : "date";
        Sort.Direction direction = "desc".equalsIgnoreCase(filterDTO.getSortOrder()) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sort = Sort.by(direction, sortField);

        if ("income".equals(filterDTO.getType())) {
            List<IncomeDTO> incomes = incomeService.filterIncomes(startDate, endDate, keyword, sort);
            List<RecentTransactionDTO> transactions = incomes.stream().map(dashboardService::incomeToTransaction).toList();
            return ResponseEntity.status(HttpStatus.OK).body(transactions);
        }
        else if ("expense".equals(filterDTO.getType())) {
            List<ExpenseDTO> expenses = expenseService.filterExpenses(startDate, endDate, keyword, sort);
            List<RecentTransactionDTO> transactions = expenses.stream().map(dashboardService::expenseToTransaction).toList();
            return ResponseEntity.status(HttpStatus.OK).body(transactions);
        }
        else if ("".equals(filterDTO.getType())) {
            List<IncomeDTO> incomes = incomeService.filterIncomes(startDate, endDate, keyword, sort);
            List<ExpenseDTO> expenses = expenseService.filterExpenses(startDate, endDate, keyword, sort);
            List<RecentTransactionDTO> transactions = dashboardService.mergeTransactions(incomes, expenses, filterDTO.getSortField(), filterDTO.getSortOrder());
            return ResponseEntity.status(HttpStatus.OK).body(transactions);
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Không có kiểu như bạn yêu cầu");
    }
}
