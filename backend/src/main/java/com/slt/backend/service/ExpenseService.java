package com.slt.backend.service;

import com.slt.backend.dto.ExpenseDto;
import com.slt.backend.entity.Expense;
import com.slt.backend.entity.User;
import com.slt.backend.repository.ExpenseRepository;
import com.slt.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
    }

    public ExpenseDto addExpense(ExpenseDto dto) {
        User user = getCurrentUser();
        Expense expense = Expense.builder()
                .user(user)
                .title(dto.getTitle())
                .category(dto.getCategory())
                .amount(dto.getAmount())
                .transactionDate(dto.getTransactionDate())
                .note(dto.getNote())
                .build();
        
        expense = expenseRepository.save(expense);
        return mapToDto(expense);
    }

    public ExpenseDto updateExpense(Long id, ExpenseDto dto) {
        Expense expense = expenseRepository.findById(id).orElseThrow(() -> new RuntimeException("Expense not found"));
        
        if (!expense.getUser().getId().equals(getCurrentUser().getId())) {
            throw new RuntimeException("Not authorized to update this expense");
        }
        
        expense.setTitle(dto.getTitle());
        expense.setCategory(dto.getCategory());
        expense.setAmount(dto.getAmount());
        expense.setTransactionDate(dto.getTransactionDate());
        expense.setNote(dto.getNote());
        
        expense = expenseRepository.save(expense);
        return mapToDto(expense);
    }

    public void deleteExpense(Long id) {
        Expense expense = expenseRepository.findById(id).orElseThrow(() -> new RuntimeException("Expense not found"));
        if (!expense.getUser().getId().equals(getCurrentUser().getId())) {
            throw new RuntimeException("Not authorized to delete this expense");
        }
        expenseRepository.delete(expense);
    }

    public List<ExpenseDto> getAllExpenses() {
        return expenseRepository.findByUserIdOrderByTransactionDateDesc(getCurrentUser().getId())
                .stream().map(this::mapToDto).collect(Collectors.toList());
    }
    
    public List<ExpenseDto> getRecentExpenses() {
        return expenseRepository.findTop5ByUserIdOrderByTransactionDateDesc(getCurrentUser().getId())
                .stream().map(this::mapToDto).collect(Collectors.toList());
    }

    private ExpenseDto mapToDto(Expense expense) {
        return ExpenseDto.builder()
                .id(expense.getId())
                .title(expense.getTitle())
                .category(expense.getCategory())
                .amount(expense.getAmount())
                .transactionDate(expense.getTransactionDate())
                .note(expense.getNote())
                .build();
    }
}
