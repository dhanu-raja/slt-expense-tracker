package com.slt.backend.repository;

import com.slt.backend.entity.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    List<Expense> findByUserIdOrderByTransactionDateDesc(Long userId);
    List<Expense> findTop5ByUserIdOrderByTransactionDateDesc(Long userId);
    
    @Query("SELECT SUM(e.amount) FROM Expense e WHERE e.user.id = :userId")
    BigDecimal sumAmountByUserId(@Param("userId") Long userId);
    
    @Query("SELECT SUM(e.amount) FROM Expense e WHERE e.user.id = :userId AND EXTRACT(MONTH FROM e.transactionDate) = :month AND EXTRACT(YEAR FROM e.transactionDate) = :year")
    BigDecimal sumAmountByUserIdAndMonth(@Param("userId") Long userId, @Param("month") int month, @Param("year") int year);
    
    @Query("SELECT e.category FROM Expense e WHERE e.user.id = :userId AND EXTRACT(MONTH FROM e.transactionDate) = :month AND EXTRACT(YEAR FROM e.transactionDate) = :year GROUP BY e.category ORDER BY SUM(e.amount) DESC LIMIT 1")
    String findHighestExpenseCategoryByUserIdAndMonth(@Param("userId") Long userId, @Param("month") int month, @Param("year") int year);
}
