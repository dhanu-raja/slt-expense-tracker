package com.slt.backend.service;

import com.slt.backend.dto.IncomeDto;
import com.slt.backend.entity.Income;
import com.slt.backend.entity.User;
import com.slt.backend.repository.IncomeRepository;
import com.slt.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IncomeService {

    private final IncomeRepository incomeRepository;
    private final UserRepository userRepository;

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
    }

    public IncomeDto addIncome(IncomeDto dto) {
        User user = getCurrentUser();
        Income income = Income.builder()
                .user(user)
                .source(dto.getSource())
                .amount(dto.getAmount())
                .receivedDate(dto.getReceivedDate())
                .note(dto.getNote())
                .build();
        
        income = incomeRepository.save(income);
        return mapToDto(income);
    }

    public IncomeDto updateIncome(Long id, IncomeDto dto) {
        Income income = incomeRepository.findById(id).orElseThrow(() -> new RuntimeException("Income not found"));
        
        if (!income.getUser().getId().equals(getCurrentUser().getId())) {
            throw new RuntimeException("Not authorized to update this income");
        }
        
        income.setSource(dto.getSource());
        income.setAmount(dto.getAmount());
        income.setReceivedDate(dto.getReceivedDate());
        income.setNote(dto.getNote());
        
        income = incomeRepository.save(income);
        return mapToDto(income);
    }

    public void deleteIncome(Long id) {
        Income income = incomeRepository.findById(id).orElseThrow(() -> new RuntimeException("Income not found"));
        if (!income.getUser().getId().equals(getCurrentUser().getId())) {
            throw new RuntimeException("Not authorized to delete this income");
        }
        incomeRepository.delete(income);
    }

    public List<IncomeDto> getAllIncomes() {
        return incomeRepository.findByUserIdOrderByReceivedDateDesc(getCurrentUser().getId())
                .stream().map(this::mapToDto).collect(Collectors.toList());
    }
    
    public List<IncomeDto> getRecentIncomes() {
        return incomeRepository.findTop5ByUserIdOrderByReceivedDateDesc(getCurrentUser().getId())
                .stream().map(this::mapToDto).collect(Collectors.toList());
    }

    private IncomeDto mapToDto(Income income) {
        return IncomeDto.builder()
                .id(income.getId())
                .source(income.getSource())
                .amount(income.getAmount())
                .receivedDate(income.getReceivedDate())
                .note(income.getNote())
                .build();
    }
}
