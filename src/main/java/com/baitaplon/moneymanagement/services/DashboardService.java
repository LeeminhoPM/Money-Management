package com.baitaplon.moneymanagement.services;

import com.baitaplon.moneymanagement.dto.ExpenseDTO;
import com.baitaplon.moneymanagement.dto.IncomeDTO;
import com.baitaplon.moneymanagement.dto.RecentTransactionDTO;
import com.baitaplon.moneymanagement.entities.ProfileEntity;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DashboardService {
    IncomeService incomeService;
    ExpenseService expenseService;
    ProfileService profileService;

    public Map<String, Object> getDashboardData() {
        ProfileEntity profile = profileService.getCurrentProfile();
        Map<String, Object> data = new LinkedHashMap<>();
        List<IncomeDTO> latestIncomes = incomeService.getLastest5IncomesForCurrentUser();
        List<ExpenseDTO> latestExpenses = expenseService.getLastest5ExpensesForCurrentUser();

//        Nối thông tin 5 thằng nhận và 5 thằng tiêu gần nhất với nhau
        List<RecentTransactionDTO> recentTransactions = Stream.concat(latestIncomes.stream().map(
                income -> RecentTransactionDTO.builder()
                        .id(income.getId())
                        .profileId(profile.getId())
                        .icon(income.getIcon())
                        .name(income.getName())
                        .amount(income.getAmount())
                        .date(income.getDate())
                        .createdAt(income.getCreatedAt())
                        .updatedAt(income.getUpdatedAt())
                        .type("Thu nhập")
                        .build()
                ), latestExpenses.stream().map(
                        expense -> RecentTransactionDTO.builder()
                                .id(expense.getId())
                                .profileId(profile.getId())
                                .icon(expense.getIcon())
                                .name(expense.getName())
                                .amount(expense.getAmount())
                                .date(expense.getDate())
                                .createdAt(expense.getCreatedAt())
                                .updatedAt(expense.getUpdatedAt())
                                .type("Chi tiêu")
                                .build()
                )
        ).sorted((RecentTransactionDTO a, RecentTransactionDTO b) -> {
            int cmp = b.getDate().compareTo(a.getDate());
            if (cmp == 0 && a.getCreatedAt() != null && b.getCreatedAt() != null) {
                return b.getCreatedAt().compareTo(a.getCreatedAt());
            }
            return cmp;
        }).collect(Collectors.toList());

        data.put(
                "totalBalance", incomeService.getTotalIncomesForCurrentUser().subtract(
                        expenseService.getTotalExpensesForCurrentUser()
                )
        );
        data.put("totalIncome", incomeService.getTotalIncomesForCurrentUser());
        data.put("totalExpense", expenseService.getTotalExpensesForCurrentUser());
        data.put("recent5Expenses", latestExpenses);
        data.put("recent5Incomes", latestIncomes);
        data.put("recentTransactions", recentTransactions);

        return data;
    }
}
