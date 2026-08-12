package com.slt.backend.service;

import com.slt.backend.dto.DashboardDto;
import com.slt.backend.entity.User;
import com.slt.backend.repository.ExpenseRepository;
import com.slt.backend.repository.IncomeRepository;
import com.slt.backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

    @Mock
    private ExpenseRepository expenseRepository;
    @Mock
    private IncomeRepository incomeRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ExpenseService expenseService;
    @Mock
    private IncomeService incomeService;
    @Mock
    private Authentication authentication;

    @InjectMocks
    private DashboardService dashboardService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder().id(1L).email("test@example.com").build();
    }

    private void mockSecurityContext() {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
        when(authentication.getName()).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
    }

    @Test
    void shouldReturnDashboardSummary() {
        mockSecurityContext();
        
        when(incomeRepository.sumAmountByUserId(1L)).thenReturn(new BigDecimal("5000.00"));
        when(expenseRepository.sumAmountByUserId(1L)).thenReturn(new BigDecimal("2000.00"));
        
        when(incomeRepository.sumAmountByUserIdAndMonth(eq(1L), anyInt(), anyInt())).thenReturn(new BigDecimal("5000.00"));
        when(expenseRepository.sumAmountByUserIdAndMonth(eq(1L), anyInt(), anyInt())).thenReturn(new BigDecimal("2000.00"));
        
        when(expenseRepository.findHighestExpenseCategoryByUserIdAndMonth(eq(1L), anyInt(), anyInt())).thenReturn("FOOD");
        
        when(expenseService.getRecentExpenses()).thenReturn(Collections.emptyList());
        when(incomeService.getRecentIncomes()).thenReturn(Collections.emptyList());

        DashboardDto summary = dashboardService.getDashboardSummary();

        assertThat(summary.getTotalIncome()).isEqualTo(new BigDecimal("5000.00"));
        assertThat(summary.getTotalExpenses()).isEqualTo(new BigDecimal("2000.00"));
        assertThat(summary.getCurrentBalance()).isEqualTo(new BigDecimal("3000.00"));
        assertThat(summary.getHighestExpenseCategory()).isEqualTo("FOOD");
    }
}
