package com.slt.backend.dto;

import com.slt.backend.entity.ExpenseCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExpenseDto {
    private Long id;
    private String title;
    private ExpenseCategory category;
    private BigDecimal amount;
    private LocalDate transactionDate;
    private String note;
}
