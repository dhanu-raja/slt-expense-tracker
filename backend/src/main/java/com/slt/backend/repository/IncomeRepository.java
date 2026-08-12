package com.slt.backend.repository;

import com.slt.backend.entity.Income;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface IncomeRepository extends JpaRepository<Income, Long> {
    List<Income> findByUserIdOrderByReceivedDateDesc(Long userId);
    List<Income> findTop5ByUserIdOrderByReceivedDateDesc(Long userId);
    
    @Query("SELECT SUM(i.amount) FROM Income i WHERE i.user.id = :userId")
    BigDecimal sumAmountByUserId(@Param("userId") Long userId);
    
    @Query("SELECT SUM(i.amount) FROM Income i WHERE i.user.id = :userId AND EXTRACT(MONTH FROM i.receivedDate) = :month AND EXTRACT(YEAR FROM i.receivedDate) = :year")
    BigDecimal sumAmountByUserIdAndMonth(@Param("userId") Long userId, @Param("month") int month, @Param("year") int year);
}
