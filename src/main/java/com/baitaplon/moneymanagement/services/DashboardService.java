package com.baitaplon.moneymanagement.services;

import com.baitaplon.moneymanagement.dto.ExpenseDTO;
import com.baitaplon.moneymanagement.dto.IncomeDTO;
import com.baitaplon.moneymanagement.dto.RecentTransactionDTO;
import com.baitaplon.moneymanagement.entities.ProfileEntity;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.Comparator;
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
        Map<String, Object> data = new LinkedHashMap<>();
        List<IncomeDTO> latestIncomes = incomeService.getLastest5IncomesForCurrentUser();
        List<ExpenseDTO> latestExpenses = expenseService.getLastest5ExpensesForCurrentUser();

//        Nối thông tin 5 thằng nhận và 5 thằng tiêu gần nhất với nhau
        List<RecentTransactionDTO> recentTransactions = mergeTransactions(latestIncomes, latestExpenses, "date", "desc");

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

    public List<RecentTransactionDTO> getTransactionsData() {
        List<IncomeDTO> incomes = incomeService.getAllIncomeForCurrentUser();
        List<ExpenseDTO> expenses = expenseService.getAllExpenseForCurrentUser();

        return mergeTransactions(incomes, expenses, "date", "desc");
    }

//    Hàm chức năng
    private static final Map<String, Comparator<RecentTransactionDTO>> SORT_MAP =
//        So sánh trường date với nhau theo thứ tự asc và phần tử có trường date là null sẽ xuống cuối
        Map.of(
            "date", Comparator.comparing(
                    RecentTransactionDTO::getDate,
                    Comparator.nullsLast(Comparator.naturalOrder())
            ),
            "amount", Comparator.comparing(
                    RecentTransactionDTO::getAmount,
                    Comparator.nullsLast(Comparator.naturalOrder())
            )
    );


    public List<RecentTransactionDTO> mergeTransactions(List<IncomeDTO> incomes, List<ExpenseDTO> expenses, String sortField, String sortOrder) {
        Comparator<RecentTransactionDTO> comparator = SORT_MAP.getOrDefault(sortField, SORT_MAP.get("date"));

        // nếu 2 thằng giống nhau thì so sánh theo thứ tự giảm dần updatedDate
        comparator = comparator.thenComparing(
                RecentTransactionDTO::getUpdatedAt,
                Comparator.nullsLast(Comparator.naturalOrder())
        );

        if ("desc".equalsIgnoreCase(sortOrder)) {
            comparator = comparator.reversed();
        }

        return Stream.concat(incomes.stream().map(
                this::incomeToTransaction
                ), expenses.stream().map(
                this::expenseToTransaction
                )
        ).sorted(comparator).collect(Collectors.toList());
    }

    public RecentTransactionDTO incomeToTransaction(IncomeDTO income) {
        ProfileEntity profile = profileService.getCurrentProfile();
        return RecentTransactionDTO.builder()
                .id(income.getId())
                .profileId(profile.getId())
                .icon(income.getIcon())
                .name(income.getName())
                .amount(income.getAmount())
                .date(income.getDate())
                .createdAt(income.getCreatedAt())
                .updatedAt(income.getUpdatedAt())
                .categoryName(income.getCategoryName())
                .type("income")
                .build();
    }

    public RecentTransactionDTO expenseToTransaction(ExpenseDTO expense) {
        ProfileEntity profile = profileService.getCurrentProfile();
        return RecentTransactionDTO.builder()
                .id(expense.getId())
                .profileId(profile.getId())
                .icon(expense.getIcon())
                .name(expense.getName())
                .amount(expense.getAmount())
                .date(expense.getDate())
                .createdAt(expense.getCreatedAt())
                .updatedAt(expense.getUpdatedAt())
                .categoryName(expense.getCategoryName())
                .type("expense")
                .build();
    }
}
