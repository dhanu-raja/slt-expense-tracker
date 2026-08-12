package com.slt.backend.repository;

import com.slt.backend.entity.Expense;
import com.slt.backend.entity.ExpenseCategory;
import com.slt.backend.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ExpenseRepositoryTest {

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private TestEntityManager entityManager;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .name("Test User")
                .email("test@example.com")
                .password("password")
                .build();
        entityManager.persistAndFlush(testUser);
    }

    @Test
    void shouldFindExpensesByUserId() {
        Expense expense = Expense.builder()
                .title("Lunch")
                .amount(new BigDecimal("15.50"))
                .category(ExpenseCategory.FOOD)
                .transactionDate(LocalDate.now())
                .user(testUser)
                .build();
        entityManager.persistAndFlush(expense);

        List<Expense> expenses = expenseRepository.findByUserIdOrderByTransactionDateDesc(testUser.getId());
        
        assertThat(expenses).hasSize(1);
        assertThat(expenses.get(0).getTitle()).isEqualTo("Lunch");
    }

    @Test
    void shouldSumAmountByUserIdAndMonth() {
        LocalDate date = LocalDate.of(2023, 10, 15);
        Expense expense1 = Expense.builder()
                .title("Lunch")
                .amount(new BigDecimal("10.00"))
                .category(ExpenseCategory.FOOD)
                .transactionDate(date)
                .user(testUser)
                .build();
        Expense expense2 = Expense.builder()
                .title("Dinner")
                .amount(new BigDecimal("25.00"))
                .category(ExpenseCategory.FOOD)
                .transactionDate(date)
                .user(testUser)
                .build();
        entityManager.persistAndFlush(expense1);
        entityManager.persistAndFlush(expense2);

        BigDecimal sum = expenseRepository.sumAmountByUserIdAndMonth(testUser.getId(), 10, 2023);
        
        // H2 might return a different scale, using compareTo
        assertThat(sum.compareTo(new BigDecimal("35.00"))).isEqualTo(0);
    }
}
