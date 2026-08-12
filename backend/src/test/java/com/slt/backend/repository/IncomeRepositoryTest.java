package com.slt.backend.repository;

import com.slt.backend.entity.Income;
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
class IncomeRepositoryTest {

    @Autowired
    private IncomeRepository incomeRepository;

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
    void shouldFindIncomesByUserId() {
        Income income = Income.builder()
                .source("Salary")
                .amount(new BigDecimal("1500.00"))
                .receivedDate(LocalDate.now())
                .user(testUser)
                .build();
        entityManager.persistAndFlush(income);

        List<Income> incomes = incomeRepository.findByUserIdOrderByReceivedDateDesc(testUser.getId());
        
        assertThat(incomes).hasSize(1);
        assertThat(incomes.get(0).getSource()).isEqualTo("Salary");
    }

    @Test
    void shouldSumAmountByUserIdAndMonth() {
        LocalDate date = LocalDate.of(2023, 10, 15);
        Income income1 = Income.builder()
                .source("Salary")
                .amount(new BigDecimal("2000.00"))
                .receivedDate(date)
                .user(testUser)
                .build();
        Income income2 = Income.builder()
                .source("Bonus")
                .amount(new BigDecimal("500.00"))
                .receivedDate(date)
                .user(testUser)
                .build();
        entityManager.persistAndFlush(income1);
        entityManager.persistAndFlush(income2);

        BigDecimal sum = incomeRepository.sumAmountByUserIdAndMonth(testUser.getId(), 10, 2023);
        
        assertThat(sum.compareTo(new BigDecimal("2500.00"))).isEqualTo(0);
    }
}
