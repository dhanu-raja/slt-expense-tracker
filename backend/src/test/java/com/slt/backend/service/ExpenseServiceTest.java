package com.slt.backend.service;

import com.slt.backend.dto.ExpenseDto;
import com.slt.backend.entity.Expense;
import com.slt.backend.entity.ExpenseCategory;
import com.slt.backend.entity.User;
import com.slt.backend.repository.ExpenseRepository;
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
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExpenseServiceTest {

    @Mock
    private ExpenseRepository expenseRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private Authentication authentication;

    @InjectMocks
    private ExpenseService expenseService;

    private User testUser;
    private Expense testExpense;

    @BeforeEach
    void setUp() {
        testUser = User.builder().id(1L).email("test@example.com").build();
        
        testExpense = Expense.builder()
                .id(100L)
                .title("Test Expense")
                .amount(new BigDecimal("10.50"))
                .category(ExpenseCategory.FOOD)
                .transactionDate(LocalDate.now())
                .user(testUser)
                .build();
    }

    private void mockSecurityContext() {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
        when(authentication.getName()).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
    }

    @Test
    void shouldAddExpense() {
        mockSecurityContext();
        
        ExpenseDto dto = ExpenseDto.builder()
                .title("New Expense")
                .amount(new BigDecimal("20.00"))
                .category(ExpenseCategory.TRANSPORT)
                .transactionDate(LocalDate.now())
                .build();

        Expense savedExpense = Expense.builder()
                .id(1L)
                .title("New Expense")
                .amount(new BigDecimal("20.00"))
                .category(ExpenseCategory.TRANSPORT)
                .transactionDate(LocalDate.now())
                .user(testUser)
                .build();
                
        when(expenseRepository.save(any(Expense.class))).thenReturn(savedExpense);

        ExpenseDto result = expenseService.addExpense(dto);

        assertThat(result.getTitle()).isEqualTo("New Expense");
        verify(expenseRepository).save(any(Expense.class));
    }

    @Test
    void shouldGetAllExpenses() {
        mockSecurityContext();
        when(expenseRepository.findByUserIdOrderByTransactionDateDesc(1L)).thenReturn(List.of(testExpense));

        List<ExpenseDto> results = expenseService.getAllExpenses();

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getTitle()).isEqualTo("Test Expense");
    }

    @Test
    void shouldDeleteExpense() {
        mockSecurityContext();
        when(expenseRepository.findById(100L)).thenReturn(Optional.of(testExpense));

        expenseService.deleteExpense(100L);

        verify(expenseRepository).delete(testExpense);
    }
}
