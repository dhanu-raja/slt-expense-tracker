package com.slt.backend.service;

import com.slt.backend.dto.IncomeDto;
import com.slt.backend.entity.Income;
import com.slt.backend.entity.User;
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
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IncomeServiceTest {

    @Mock
    private IncomeRepository incomeRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private Authentication authentication;

    @InjectMocks
    private IncomeService incomeService;

    private User testUser;
    private Income testIncome;

    @BeforeEach
    void setUp() {
        testUser = User.builder().id(1L).email("test@example.com").build();
        
        testIncome = Income.builder()
                .id(100L)
                .source("Salary")
                .amount(new BigDecimal("1500.00"))
                .receivedDate(LocalDate.now())
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
    void shouldAddIncome() {
        mockSecurityContext();
        
        IncomeDto dto = IncomeDto.builder()
                .source("Bonus")
                .amount(new BigDecimal("500.00"))
                .receivedDate(LocalDate.now())
                .build();

        Income savedIncome = Income.builder()
                .id(1L)
                .source("Bonus")
                .amount(new BigDecimal("500.00"))
                .receivedDate(LocalDate.now())
                .user(testUser)
                .build();
                
        when(incomeRepository.save(any(Income.class))).thenReturn(savedIncome);

        IncomeDto result = incomeService.addIncome(dto);

        assertThat(result.getSource()).isEqualTo("Bonus");
        verify(incomeRepository).save(any(Income.class));
    }

    @Test
    void shouldGetAllIncomes() {
        mockSecurityContext();
        when(incomeRepository.findByUserIdOrderByReceivedDateDesc(1L)).thenReturn(List.of(testIncome));

        List<IncomeDto> results = incomeService.getAllIncomes();

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getSource()).isEqualTo("Salary");
    }

    @Test
    void shouldDeleteIncome() {
        mockSecurityContext();
        when(incomeRepository.findById(100L)).thenReturn(Optional.of(testIncome));

        incomeService.deleteIncome(100L);

        verify(incomeRepository).delete(testIncome);
    }
}
