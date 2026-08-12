package com.slt.backend.service;

import com.slt.backend.dto.DashboardDto;
import com.slt.backend.entity.User;
import com.slt.backend.repository.ExpenseRepository;
import com.slt.backend.repository.IncomeRepository;
import com.slt.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final ExpenseRepository expenseRepository;
    private final IncomeRepository incomeRepository;
    private final UserRepository userRepository;
    private final ExpenseService expenseService;
    private final IncomeService incomeService;

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
    }

    public DashboardDto getDashboardSummary() {
        Long userId = getCurrentUser().getId();
        LocalDate now = LocalDate.now();
        int month = now.getMonthValue();
        int year = now.getYear();

        BigDecimal totalIncome = incomeRepository.sumAmountByUserId(userId);
        if (totalIncome == null) totalIncome = BigDecimal.ZERO;

        BigDecimal totalExpenses = expenseRepository.sumAmountByUserId(userId);
        if (totalExpenses == null) totalExpenses = BigDecimal.ZERO;

        BigDecimal monthlyIncome = incomeRepository.sumAmountByUserIdAndMonth(userId, month, year);
        if (monthlyIncome == null) monthlyIncome = BigDecimal.ZERO;

        BigDecimal monthlyExpenses = expenseRepository.sumAmountByUserIdAndMonth(userId, month, year);
        if (monthlyExpenses == null) monthlyExpenses = BigDecimal.ZERO;

        String highestExpenseCategory = expenseRepository.findHighestExpenseCategoryByUserIdAndMonth(userId, month, year);

        return DashboardDto.builder()
                .totalIncome(totalIncome)
                .totalExpenses(totalExpenses)
                .currentBalance(totalIncome.subtract(totalExpenses))
                .monthlyIncome(monthlyIncome)
                .monthlyExpenses(monthlyExpenses)
                .highestExpenseCategory(highestExpenseCategory)
                .recentExpenses(expenseService.getRecentExpenses())
                .recentIncomes(incomeService.getRecentIncomes())
                .build();
    }
}
